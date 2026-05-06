package com.swapper.monolith.ItemService.dto.UserGamePost;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SetFeaturedPriorityRequest {
    @Min(0)
    int priority;
}
