package com.swapper.monolith.ItemService.controller;

import com.swapper.monolith.ItemService.service.GenreService;
import com.swapper.monolith.ItemService.service.PlatformService;
import com.swapper.monolith.external.dto.GenreDto;
import com.swapper.monolith.external.dto.PlatformDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/data")
public class GameDataController {
    private final GenreService genreService;
    private final PlatformService platformService;
    @GetMapping("/genres")
    public ResponseEntity<List<GenreDto>> getGenre() {
        return ResponseEntity.ok(genreService.getGenres());
    }
    @GetMapping("/platforms")
    public ResponseEntity<List<PlatformDto>> getPlatforms() {
        return ResponseEntity.ok(platformService.getPlatforms());
    }
}
