package com.swapper.monolith.ChatService.dtos;

import com.swapper.monolith.ChatService.dtos.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class SendMessageDto {

    @NotBlank(message = "conversationId is required")
    private String conversationId;

    @NotNull(message = "messageType is required")
    private MessageType messageType;

    private String content;

    private Long presetId;

    private Instant meetupAt;

    private String location;
}
