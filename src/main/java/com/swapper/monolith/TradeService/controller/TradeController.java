package com.swapper.monolith.TradeService.controller;

import com.swapper.monolith.TradeService.dto.CreateTradeRequest;
import com.swapper.monolith.TradeService.dto.TradeResponse;
import com.swapper.monolith.TradeService.service.TradeService;
import com.swapper.monolith.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trades/v1")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER')")
public class TradeController {

    private final TradeService tradeService;

    @PostMapping
    public ResponseEntity<TradeResponse> initiateTrade(@Valid @RequestBody CreateTradeRequest request) {
        return ResponseEntity.ok(tradeService.initiateTrade(request, getPrincipal()));
    }

    @GetMapping
    public ResponseEntity<List<TradeResponse>> getTradesForUser() {
        return ResponseEntity.ok(tradeService.getTradesForUser(getPrincipal()));
    }

    @GetMapping("/{tradeId}")
    public ResponseEntity<TradeResponse> getTradeById(@PathVariable String tradeId) {
        return ResponseEntity.ok(tradeService.getTradeById(tradeId, getPrincipal()));
    }

    @PatchMapping("/{tradeId}/accept")
    public ResponseEntity<TradeResponse> acceptTrade(@PathVariable String tradeId) {
        return ResponseEntity.ok(tradeService.acceptTrade(tradeId, getPrincipal()));
    }

    @PatchMapping("/{tradeId}/decline")
    public ResponseEntity<TradeResponse> declineTrade(@PathVariable String tradeId) {
        return ResponseEntity.ok(tradeService.declineTrade(tradeId, getPrincipal()));
    }

    @PatchMapping("/{tradeId}/complete")
    public ResponseEntity<TradeResponse> completeTrade(@PathVariable String tradeId) {
        return ResponseEntity.ok(tradeService.completeTrade(tradeId, getPrincipal()));
    }

    @PatchMapping("/{tradeId}/cancel")
    public ResponseEntity<TradeResponse> cancelTrade(@PathVariable String tradeId) {
        return ResponseEntity.ok(tradeService.cancelTrade(tradeId, getPrincipal()));
    }

    private UserDetailsImpl getPrincipal() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
