package com.swapper.monolith.ItemService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CoverRequest {
    @NotEmpty
    @JsonProperty("game_ids")
    List<Long> gameIds;
}
