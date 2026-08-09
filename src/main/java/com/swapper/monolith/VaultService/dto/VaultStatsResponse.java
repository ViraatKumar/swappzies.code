package com.swapper.monolith.VaultService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VaultStatsResponse {
    long total;
    long favorites;
    long notStarted;
    long inProgress;
    long completed;
    long abandoned;
}
