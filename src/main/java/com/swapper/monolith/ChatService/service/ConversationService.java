package com.swapper.monolith.ChatService.service;

import com.swapper.monolith.ChatService.dtos.ConversationDto;
import com.swapper.monolith.ChatService.entity.Conversation;
import com.swapper.monolith.ChatService.entity.ConversationParticipant;
import com.swapper.monolith.ChatService.repository.ChatMessageRepository;
import com.swapper.monolith.ChatService.repository.ConversationParticipantRepository;
import com.swapper.monolith.ChatService.repository.ConversationRepository;
import com.swapper.monolith.event.TradeAcceptedEvent;
import com.swapper.monolith.exception.CustomExceptions.ForbiddenException;
import com.swapper.monolith.exception.CustomExceptions.ResourceNotFoundException;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.User;
import com.swapper.monolith.repository.UserRepository;
import com.swapper.monolith.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @EventListener
    @Transactional
    public void onTradeAccepted(TradeAcceptedEvent event) {
        try {
            User initiator = userRepository.findByUserId(event.getInitiatorUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(ApiResponses.CHAT_COUNTERPARTY_NOT_FOUND.getMessage()));
            User receiver = userRepository.findByUserId(event.getReceiverUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(ApiResponses.CHAT_COUNTERPARTY_NOT_FOUND.getMessage()));
            findOrCreateConversation(initiator, receiver);
        } catch (Exception e) {
            log.error("Failed to create conversation for accepted trade {}: {}", event.getTradeId(), e.getMessage(), e);
        }
    }

    @Transactional
    public Conversation findOrCreateConversation(User a, User b) {
        User first;
        User second;
        if (a.getUserId().compareTo(b.getUserId()) <= 0) {
            first = a;
            second = b;
        } else {
            first = b;
            second = a;
        }

        return conversationRepository.findByParticipantAUserIdAndParticipantBUserId(
                        first.getUserId(), second.getUserId())
                .orElseGet(() -> {
                    Conversation conversation = new Conversation();
                    conversation.setParticipantA(first);
                    conversation.setParticipantB(second);
                    Conversation saved = conversationRepository.save(conversation);

                    ConversationParticipant participantA = new ConversationParticipant();
                    participantA.setConversation(saved);
                    participantA.setUser(first);
                    participantRepository.save(participantA);

                    ConversationParticipant participantB = new ConversationParticipant();
                    participantB.setConversation(saved);
                    participantB.setUser(second);
                    participantRepository.save(participantB);

                    log.info("Created conversation {} between {} and {}",
                            saved.getConversationId(), first.getUserId(), second.getUserId());
                    return saved;
                });
    }

    public List<ConversationDto> listConversations(UserDetailsImpl principal) {
        return conversationRepository.findAllForUser(principal.getUserId()).stream()
                .map(c -> ConversationDto.from(c, principal.getUserId(), computeUnreadCount(c, principal.getUserId())))
                .collect(Collectors.toList());
    }

    public ConversationDto getConversation(String conversationId, UserDetailsImpl principal) {
        Conversation conversation = getConversationEntity(conversationId);
        assertParticipant(conversation, principal.getUserId());
        return ConversationDto.from(conversation, principal.getUserId(),
                computeUnreadCount(conversation, principal.getUserId()));
    }

    public Conversation getConversationEntity(String conversationId) {
        return conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException(ApiResponses.CHAT_CONVERSATION_NOT_FOUND.getMessage()));
    }

    public void assertParticipant(Conversation conversation, String userId) {
        boolean isParticipant = conversation.getParticipantA().getUserId().equals(userId)
                || conversation.getParticipantB().getUserId().equals(userId);
        if (!isParticipant) {
            throw new ForbiddenException(ApiResponses.CHAT_NOT_A_PARTICIPANT.getMessage());
        }
    }

    @Transactional
    public void markRead(String conversationId, Long lastReadMessageId, UserDetailsImpl principal) {
        Conversation conversation = getConversationEntity(conversationId);
        assertParticipant(conversation, principal.getUserId());

        ConversationParticipant participant = participantRepository
                .findByConversationAndUserUserId(conversation, principal.getUserId())
                .orElseThrow(() -> new ForbiddenException(ApiResponses.CHAT_NOT_A_PARTICIPANT.getMessage()));

        if (participant.getLastReadMessageId() == null
                || lastReadMessageId > participant.getLastReadMessageId()) {
            participant.setLastReadMessageId(lastReadMessageId);
            participant.setLastReadAt(Instant.now());
            participantRepository.save(participant);
        }
    }

    @Transactional
    public void touchAfterMessage(Conversation conversation, Instant at, String preview) {
        conversation.setLastMessageAt(at);
        conversation.setLastMessagePreview(preview);
        conversationRepository.save(conversation);
    }

    private long computeUnreadCount(Conversation conversation, String userId) {
        ConversationParticipant participant = participantRepository
                .findByConversationAndUserUserId(conversation, userId)
                .orElse(null);
        if (participant == null) {
            return 0L;
        }
        if (participant.getLastReadMessageId() == null) {
            return chatMessageRepository.countByConversationAndSenderUserIdNot(conversation, userId);
        }
        return chatMessageRepository.countByConversationAndIdGreaterThanAndSenderUserIdNot(
                conversation, participant.getLastReadMessageId(), userId);
    }
}
