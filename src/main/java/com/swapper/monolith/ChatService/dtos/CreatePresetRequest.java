package com.swapper.monolith.ChatService.dtos;

import com.swapper.monolith.ChatService.dtos.enums.PresetCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreatePresetRequest {

    @NotNull(message = "category is required")
    private PresetCategory category;

    @NotBlank(message = "content is required")
    @Size(max = 500, message = "content must be 500 characters or fewer")
    private String content;

    @PositiveOrZero(message = "displayOrder must be zero or positive")
    private Integer displayOrder;

    private Boolean isActive;
}
