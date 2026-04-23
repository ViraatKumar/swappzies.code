package com.swapper.monolith.ItemService.controller;

import com.swapper.monolith.ItemService.dto.CreateListingRequest;
import com.swapper.monolith.ItemService.dto.GameDto;
import com.swapper.monolith.ItemService.dto.GameFilterRequest;
import com.swapper.monolith.ItemService.dto.GameSearchResponse;
import com.swapper.monolith.ItemService.service.GameService;
import com.swapper.monolith.dto.ApiResponse;
import com.swapper.monolith.exception.enums.ApiResponses;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/filter")
    public ResponseEntity<Page<GameDto>> filterGames(
            @RequestBody GameFilterRequest filter) {
        return ResponseEntity.ok(gameService.filterGames(filter));
    }

    @PostMapping("/listing")
    public ResponseEntity<GameSearchResponse> createListing(@RequestBody CreateListingRequest createListingRequest){
        return null;
    }
}
