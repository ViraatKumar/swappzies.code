package com.swapper.monolith.WishlistService.controller;

import com.swapper.monolith.WishlistService.dto.AddToWishlistRequest;
import com.swapper.monolith.WishlistService.dto.WishlistItemDto;
import com.swapper.monolith.WishlistService.service.WishlistService;
import com.swapper.monolith.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/wishlist")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER')")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishlistItemDto>> getWishlist() {
        UserDetailsImpl principal = getPrincipal();
        return ResponseEntity.ok(wishlistService.getWishlist(principal));
    }

    @PostMapping
    public ResponseEntity<WishlistItemDto> addToWishlist(@Valid @RequestBody AddToWishlistRequest request) {
        UserDetailsImpl principal = getPrincipal();
        return ResponseEntity.ok(wishlistService.addToWishlist(request, principal));
    }

    @DeleteMapping("/{wishlistItemId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable String wishlistItemId) {
        UserDetailsImpl principal = getPrincipal();
        wishlistService.removeFromWishlist(wishlistItemId, principal);
        return ResponseEntity.noContent().build();
    }

    private UserDetailsImpl getPrincipal() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
