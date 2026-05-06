package com.swapper.monolith.ItemService.controller;

import com.swapper.monolith.ItemService.dto.UserGamePost.SetFeaturedPriorityRequest;
import com.swapper.monolith.ItemService.dto.UserGamePost.UserGamePostDto;
import com.swapper.monolith.ItemService.service.ItemListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/listings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class ItemListingAdminController {

    private final ItemListingService itemListingService;

    @PatchMapping("/{listingId}/featured")
    public ResponseEntity<UserGamePostDto> setFeaturedPriority(
            @PathVariable String listingId,
            @Valid @RequestBody SetFeaturedPriorityRequest request) {
        return ResponseEntity.ok(itemListingService.setFeaturedPriority(listingId, request.getPriority()));
    }
}
