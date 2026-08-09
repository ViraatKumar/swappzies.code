package com.swapper.monolith.VaultService.dto;

import com.swapper.monolith.ItemService.constants.Platform;
import com.swapper.monolith.VaultService.dto.constant.CompletionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateVaultEntryRequest {

    long gameId;

    @NotNull
    String platform;

    CompletionStatus completion;

    Boolean favorite;
}
