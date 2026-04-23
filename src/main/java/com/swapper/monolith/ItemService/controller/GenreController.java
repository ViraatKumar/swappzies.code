package com.swapper.monolith.ItemService.controller;

import com.swapper.monolith.ItemService.service.GenreService;
import com.swapper.monolith.external.dto.GenreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/genre")
public class GenreController {
    private final GenreService genreService;
    @GetMapping
    public ResponseEntity<List<GenreDto>> getGenre() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }
}
