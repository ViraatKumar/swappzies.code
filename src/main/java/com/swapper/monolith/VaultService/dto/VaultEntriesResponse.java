package com.swapper.monolith.VaultService.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VaultEntriesResponse {
    List<VaultEntryResponse> userVaultEntries;
    String gamerTitle;
    String gamerLevelName;


    public static VaultEntriesResponse createVaultEntriesResponse(List<VaultEntryResponse> userVaultEntries,
                                                                  String gamerTitle,
                                                                  String gamerLevelName) {
        return VaultEntriesResponse
                .builder()
                .userVaultEntries(userVaultEntries)
                .gamerTitle(gamerTitle)
                .gamerLevelName(gamerLevelName)
                .build();
    }
}
