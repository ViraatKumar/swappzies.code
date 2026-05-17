package com.swapper.monolith.ItemService.controller;

import com.swapper.monolith.ItemService.dto.CoverDto;
import com.swapper.monolith.ItemService.dto.CoverRequest;
import com.swapper.monolith.ItemService.service.CoverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cover")
@RequiredArgsConstructor
public class CoverController {

    final CoverService coverService;
    @GetMapping
    public ResponseEntity<List<CoverDto>> getCoverUrl(@RequestBody @Valid CoverRequest coverRequest) {
        return ResponseEntity.ok(coverService.getCoverUrls(coverRequest));
    }
}
