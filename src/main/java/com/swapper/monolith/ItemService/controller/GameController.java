package com.swapper.monolith.ItemService.controller;

import com.swapper.monolith.ItemService.dto.GameSearchResponse;
import com.swapper.monolith.ItemService.service.GameService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER')")
@Validated
public class GameController {

    private final GameService gameService;

    @GetMapping
    public ResponseEntity<GameSearchResponse> searchGameByName(
            @RequestParam(name = "gameName") @Size(min = 4, message = "gameName must be at least 4 characters") String gameName) {
        return ResponseEntity.ok(gameService.getGameByName(gameName));
    }
}
