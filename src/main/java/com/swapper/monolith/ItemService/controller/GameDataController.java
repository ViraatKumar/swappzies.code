package com.swapper.monolith.ItemService.controller;

import com.swapper.monolith.ItemService.dto.GameDataValues;
import com.swapper.monolith.ItemService.service.GenreService;
import com.swapper.monolith.ItemService.service.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/data")
public class GameDataController {
    private final GenreService genreService;
    private final PlatformService platformService;
    @GetMapping("/genres")
    public ResponseEntity<GameDataValues> getGenre() {
        return ResponseEntity.ok(genreService.getGenres());
    }
    @GetMapping("/platforms")
    public ResponseEntity<GameDataValues> getPlatforms() {
        return ResponseEntity.ok(platformService.getPlatforms());
    }



}
