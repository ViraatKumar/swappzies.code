package com.swapper.monolith.VaultService.controller;

import com.swapper.monolith.VaultService.dto.*;
import com.swapper.monolith.VaultService.service.VaultService;
import com.swapper.monolith.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vault")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER')")
public class VaultController {

    private final VaultService vaultService;

    @GetMapping("/entries")
    public ResponseEntity<VaultEntriesResponse> getEntries() {
        return ResponseEntity.ok(vaultService.getUserVaultEntries(getPrincipal()));
    }

    @PostMapping("/entries")
    public ResponseEntity<VaultEntryResponse> createEntry(@Valid @RequestBody CreateVaultEntryRequest request) {
        return ResponseEntity.ok(vaultService.createEntry(request, getPrincipal()));
    }

    @PatchMapping("/entries/{id}")
    public ResponseEntity<VaultEntryResponse> updateEntry(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateVaultEntryRequest request) {
        return ResponseEntity.ok(vaultService.updateEntry(id, request, getPrincipal()));
    }

    @DeleteMapping("/entries/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable("id") String id) {
        vaultService.deleteEntry(id, getPrincipal());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/entries/{id}/favorite")
    public ResponseEntity<VaultEntryResponse> toggleFavorite(@PathVariable("id") String id) {
        return ResponseEntity.ok(vaultService.toggleFavorite(id, getPrincipal()));
    }

    @GetMapping("/stats")
    public ResponseEntity<VaultStatsResponse> getStats() {
        return ResponseEntity.ok(vaultService.getStats(getPrincipal()));
    }

    private UserDetailsImpl getPrincipal() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
