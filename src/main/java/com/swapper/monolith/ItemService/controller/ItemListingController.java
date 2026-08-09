package com.swapper.monolith.ItemService.controller;

import com.swapper.monolith.ItemService.dto.ListingFilterRequest;
import com.swapper.monolith.ItemService.dto.UserGamePost.CreateListingRequest;
import com.swapper.monolith.ItemService.dto.UserGamePost.UpdateListingRequest;
import com.swapper.monolith.ItemService.dto.UserGamePost.UserGamePostDto;
import com.swapper.monolith.ItemService.service.ItemListingService;
import com.swapper.monolith.service.UserDetailsImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
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
public class ItemListingController {

    private final ItemListingService itemListingService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<UserGamePostDto> createListing(@Valid @RequestBody CreateListingRequest request) {
        return ResponseEntity.ok().body(itemListingService.createListing(request));
    }

    @GetMapping
    public ResponseEntity<Page<UserGamePostDto>> filterListings(@ModelAttribute ListingFilterRequest request) {
        return ResponseEntity.ok(itemListingService.filterListings(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<Page<UserGamePostDto>> getUsersListings(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="10") @Max(value=100) int pageSize,String fetchType) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(itemListingService.getUserActiveGamePosts(principal,page,pageSize, fetchType));
    }

    @GetMapping("/{listingId}")
    public ResponseEntity<UserGamePostDto> getListingById(@PathVariable String listingId) {
        return ResponseEntity.ok(itemListingService.getListingById(listingId));
    }

    @PatchMapping("/{listingId}")
    @PreAuthorize("hasAnyAuthority('USER')")
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
}
