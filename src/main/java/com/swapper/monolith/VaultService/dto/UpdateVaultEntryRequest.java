package com.swapper.monolith.VaultService.dto;

import com.swapper.monolith.ItemService.constants.Platform;
import com.swapper.monolith.VaultService.dto.constant.CompletionStatus;
import lombok.Data;

@Data
public class UpdateVaultEntryRequest {
    Platform platform;
    CompletionStatus completion;
    Boolean favorite;
}
