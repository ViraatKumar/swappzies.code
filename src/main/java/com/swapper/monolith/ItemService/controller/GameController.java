package com.swapper.monolith.ItemService.controller;

import com.swapper.monolith.ItemService.dto.GameSearchResponse;
import com.swapper.monolith.ItemService.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER')")
public class GameController {

    private final GameService gameService;
    @GetMapping
    public ResponseEntity<GameSearchResponse> searchGameByName(@RequestParam(name="gameName") String gameName){
        return ResponseEntity.ok(gameService.getGameByName(gameName));
    }
}
