package com.swapper.monolith.TradeService.controller;

import com.swapper.monolith.TradeService.dto.CreateRentalRequest;
import com.swapper.monolith.TradeService.dto.RentalResponse;
import com.swapper.monolith.TradeService.service.RentalService;
import com.swapper.monolith.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals/v1")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER')")
public class RentalController {

    private final RentalService rentalService;

    @PostMapping
    public ResponseEntity<RentalResponse> initiateRental(@Valid @RequestBody CreateRentalRequest request) {
        return ResponseEntity.ok(rentalService.initiateRental(request, getPrincipal()));
    }

    @GetMapping
    public ResponseEntity<List<RentalResponse>> getRentalsForUser() {
        return ResponseEntity.ok(rentalService.getRentalsForUser(getPrincipal()));
    }

    @PatchMapping("/{rentalId}/accept")
    public ResponseEntity<RentalResponse> acceptRental(@PathVariable String rentalId) {
        return ResponseEntity.ok(rentalService.acceptRental(rentalId, getPrincipal()));
    }

    @PatchMapping("/{rentalId}/cancel")
    public ResponseEntity<RentalResponse> cancelRental(@PathVariable String rentalId) {
        return ResponseEntity.ok(rentalService.cancelRental(rentalId, getPrincipal()));
    }

    @PatchMapping("/{rentalId}/return")
    public ResponseEntity<RentalResponse> markReturned(@PathVariable String rentalId) {
        return ResponseEntity.ok(rentalService.markReturned(rentalId, getPrincipal()));
    }

    private UserDetailsImpl getPrincipal() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
