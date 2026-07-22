package com.swapper.monolith.ChatService.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MarkReadRequest {

    @NotNull(message = "lastReadMessageId is required")
    private Long lastReadMessageId;
}
