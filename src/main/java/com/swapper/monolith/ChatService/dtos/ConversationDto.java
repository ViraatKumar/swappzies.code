package com.swapper.monolith.ChatService.dtos;

import com.swapper.monolith.ChatService.entity.Conversation;
import com.swapper.monolith.model.User;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ConversationDto {

    private final String conversationId;
    private final String counterpartyUserId;
    private final String counterpartyUsername;
    private final String counterpartyDisplayName;
    private final String counterpartyAvatarUrl;
    private final Instant lastMessageAt;
    private final String lastMessagePreview;
    private final long unreadCount;

    public static ConversationDto from(Conversation conversation, String currentUserId, long unreadCount) {
        User counterparty = conversation.getParticipantA().getUserId().equals(currentUserId)
                ? conversation.getParticipantB()
                : conversation.getParticipantA();
        return ConversationDto.builder()
                .conversationId(conversation.getConversationId())
                .counterpartyUserId(counterparty.getUserId())
                .counterpartyUsername(counterparty.getUsername())
                .counterpartyDisplayName(counterparty.getDisplayName())
                .counterpartyAvatarUrl(counterparty.getAvatarUrl())
                .lastMessageAt(conversation.getLastMessageAt())
                .lastMessagePreview(conversation.getLastMessagePreview())
                .unreadCount(unreadCount)
                .build();
    }
}
