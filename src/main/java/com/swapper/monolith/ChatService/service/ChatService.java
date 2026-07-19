package com.swapper.monolith.ChatService.service;

import com.swapper.monolith.ChatService.dtos.MessageDto;
import com.swapper.monolith.ChatService.dtos.SendMessageDto;
import com.swapper.monolith.ChatService.dtos.enums.MessageType;
import com.swapper.monolith.ChatService.entity.ChatMessage;
import com.swapper.monolith.ChatService.entity.ChatPresetMessage;
import com.swapper.monolith.ChatService.entity.Conversation;
import com.swapper.monolith.ChatService.repository.ChatMessageRepository;
import com.swapper.monolith.ChatService.repository.ChatPresetMessageRepository;
import com.swapper.monolith.exception.CustomExceptions.DuplicatedResourceException;
import com.swapper.monolith.exception.CustomExceptions.ResourceNotFoundException;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.User;
import com.swapper.monolith.repository.UserRepository;
import com.swapper.monolith.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int PREVIEW_MAX = 240;

    private final ChatMessageRepository chatMessageRepository;
    private final ChatPresetMessageRepository presetMessageRepository;
    private final ConversationService conversationService;
    private final UserRepository userRepository;

    @Transactional
    public MessageDto sendMessage(SendMessageDto request, UserDetailsImpl principal) {
        Conversation conversation = conversationService.getConversationEntity(request.getConversationId());
        conversationService.assertParticipant(conversation, principal.getUserId());

        validateMessageShape(request);

        User sender = userRepository.findByUserId(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender user not found"));

        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setMessageType(request.getMessageType());

        switch (request.getMessageType()) {
            case PLAIN_TEXT -> message.setContent(request.getContent());
            case PRESET -> {
                ChatPresetMessage preset = presetMessageRepository.findById(request.getPresetId())
                        .orElseThrow(() -> new ResourceNotFoundException(ApiResponses.CHAT_PRESET_NOT_FOUND.getMessage()));
                if (Boolean.FALSE.equals(preset.getIsActive())) {
                    throw new DuplicatedResourceException(ApiResponses.CHAT_PRESET_INACTIVE);
                }
                message.setPresetId(preset.getId());
                message.setContent(preset.getContent());
            }
            case MEETUP -> {
                message.setContent(request.getContent());
                message.setMeetupAt(request.getMeetupAt());
                message.setLocation(request.getLocation());
            }
        }

        ChatMessage saved = chatMessageRepository.save(message);
        conversationService.touchAfterMessage(conversation, saved.getCreatedAt(),
                buildPreview(saved));

        return MessageDto.from(saved);
    }

    public Page<MessageDto> getMessages(String conversationId, int page, int size, UserDetailsImpl principal) {
        Conversation conversation = conversationService.getConversationEntity(conversationId);
        conversationService.assertParticipant(conversation, principal.getUserId());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return chatMessageRepository.findByConversationOrderByCreatedAtDesc(conversation, pageable)
                .map(MessageDto::from);
    }

    private void validateMessageShape(SendMessageDto request) {
        MessageType type = request.getMessageType();
        boolean hasContent = request.getContent() != null && !request.getContent().isBlank();
        boolean hasPreset = request.getPresetId() != null;
        boolean hasMeetupFields = request.getMeetupAt() != null
                || (request.getLocation() != null && !request.getLocation().isBlank());

        switch (type) {
            case PLAIN_TEXT -> {
                if (!hasContent || hasPreset) {
                    throw new DuplicatedResourceException(ApiResponses.CHAT_INVALID_MESSAGE_TYPE_FIELDS);
                }
            }
            case PRESET -> {
                if (!hasPreset || hasContent) {
                    throw new DuplicatedResourceException(ApiResponses.CHAT_INVALID_MESSAGE_TYPE_FIELDS);
                }
            }
            case MEETUP -> {
                if (hasPreset) {
                    throw new DuplicatedResourceException(ApiResponses.CHAT_INVALID_MESSAGE_TYPE_FIELDS);
                }
                if (!hasContent && !hasMeetupFields) {
                    throw new DuplicatedResourceException(ApiResponses.CHAT_INVALID_MESSAGE_TYPE_FIELDS);
                }
            }
        }
    }

    private String buildPreview(ChatMessage message) {
        String base = switch (message.getMessageType()) {
            case PLAIN_TEXT, PRESET -> message.getContent() == null ? "" : message.getContent();
            case MEETUP -> {
                String text = message.getContent() != null ? message.getContent() : "";
                String location = message.getLocation() != null ? message.getLocation() : "";
                yield ("[Meetup] " + text + (location.isBlank() ? "" : " @ " + location)).trim();
            }
        };
        return base.length() > PREVIEW_MAX ? base.substring(0, PREVIEW_MAX) : base;
    }
}
