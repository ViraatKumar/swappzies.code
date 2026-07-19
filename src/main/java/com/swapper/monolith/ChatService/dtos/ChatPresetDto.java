package com.swapper.monolith.ChatService.dtos;

import com.swapper.monolith.ChatService.dtos.enums.PresetCategory;
import com.swapper.monolith.ChatService.entity.ChatPresetMessage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatPresetDto {
    private final Long id;
    private final PresetCategory category;
    private final String content;
    private final Integer displayOrder;
    private final Boolean isActive;

    public static ChatPresetDto from(ChatPresetMessage preset) {
        return ChatPresetDto.builder()
                .id(preset.getId())
                .category(preset.getCategory())
                .content(preset.getContent())
                .displayOrder(preset.getDisplayOrder())
                .isActive(preset.getIsActive())
                .build();
    }
}
