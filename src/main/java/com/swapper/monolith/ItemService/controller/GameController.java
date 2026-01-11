package com.swapper.monolith.ItemService.controller;

import com.swapper.monolith.ItemService.dto.GameDTO;
import com.swapper.monolith.ItemService.dto.TwitchSearchResponse;
import com.swapper.monolith.ItemService.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER')")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameDTO> createGame(@RequestBody GameDTO gameDTO) {
        GameDTO createdGame = gameService.createGame(gameDTO);
        return new ResponseEntity<>(createdGame, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<TwitchSearchResponse> searchGameByName(@RequestParam(name="gameName") String gameName){
        return ResponseEntity.ok(gameService.getGameByName(gameName));
    }
}
