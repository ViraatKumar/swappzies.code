package com.swapper.monolith.ChatService.dtos;

import com.swapper.monolith.ChatService.dtos.enums.MessageType;
import com.swapper.monolith.ChatService.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class MessageDto {
    private final Long id;
    private final String conversationId;
    private final String senderId;
    private final String senderUsername;
    private final MessageType messageType;
    private final String content;
    private final Long presetId;
    private final Instant meetupAt;
    private final String location;
    private final Instant createdAt;

    public static MessageDto from(ChatMessage message) {
        return MessageDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getConversationId())
                .senderId(message.getSender().getUserId())
                .senderUsername(message.getSender().getUsername())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .presetId(message.getPresetId())
                .meetupAt(message.getMeetupAt())
                .location(message.getLocation())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
