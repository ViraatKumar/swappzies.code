package com.swapper.monolith.ItemService.controller;

import com.swapper.monolith.ItemService.dto.ListingFilterRequest;
import com.swapper.monolith.ItemService.dto.UserGamePost.CreateListingRequest;
import com.swapper.monolith.ItemService.dto.UserGamePost.UpdateListingRequest;
import com.swapper.monolith.ItemService.dto.UserGamePost.UserGamePostDto;
import com.swapper.monolith.ItemService.service.ItemListingService;
import com.swapper.monolith.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER')")
public class ItemListingController {

    private final ItemListingService itemListingService;

    @PostMapping
    public ResponseEntity<UserGamePostDto> createListing(@Valid @RequestBody CreateListingRequest request) {
        return ResponseEntity.ok().body(itemListingService.createListing(request));
    }

    @GetMapping
    public ResponseEntity<Page<UserGamePostDto>> filterListings(@ModelAttribute ListingFilterRequest request) {
        return ResponseEntity.ok(itemListingService.filterListings(request));
    }

    @GetMapping("/{listingId}")
    public ResponseEntity<UserGamePostDto> getListingById(@PathVariable String listingId) {
        return ResponseEntity.ok(itemListingService.getListingById(listingId));
    }

    @PatchMapping("/{listingId}")
    public ResponseEntity<UserGamePostDto> updateListing(
            @PathVariable String listingId,
            @RequestBody UpdateListingRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return ResponseEntity.ok(itemListingService.updateListing(listingId, request, principal));
    }

    @DeleteMapping("/{listingId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteListing(@PathVariable String listingId) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        itemListingService.deleteListing(listingId, principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Page<UserGamePostDto>> getUsersListings() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(itemListingService.getUserActiveGamePosts(principal));
    }
}
