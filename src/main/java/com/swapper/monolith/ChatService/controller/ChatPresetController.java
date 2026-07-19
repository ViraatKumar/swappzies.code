package com.swapper.monolith.ChatService.controller;

import com.swapper.monolith.ChatService.dtos.ChatPresetDto;
import com.swapper.monolith.ChatService.dtos.enums.PresetCategory;
import com.swapper.monolith.ChatService.service.ChatPresetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/v1/presets")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER')")
public class ChatPresetController {

    private final ChatPresetService chatPresetService;

    @GetMapping
    public ResponseEntity<List<ChatPresetDto>> listPresets(
            @RequestParam(required = false) PresetCategory category) {
        return ResponseEntity.ok(chatPresetService.listActivePresets(category));
    }
}
