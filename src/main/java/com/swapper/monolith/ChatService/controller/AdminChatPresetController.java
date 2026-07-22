package com.swapper.monolith.ChatService.controller;

import com.swapper.monolith.ChatService.dtos.ChatPresetDto;
import com.swapper.monolith.ChatService.dtos.CreatePresetRequest;
import com.swapper.monolith.ChatService.dtos.UpdatePresetRequest;
import com.swapper.monolith.ChatService.service.ChatPresetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/v1/chat-presets")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminChatPresetController {

    private final ChatPresetService chatPresetService;

    @GetMapping
    public ResponseEntity<List<ChatPresetDto>> listAll() {
        return ResponseEntity.ok(chatPresetService.listAllPresets());
    }

    @PostMapping
    public ResponseEntity<ChatPresetDto> create(@Valid @RequestBody CreatePresetRequest request) {
        return ResponseEntity.ok(chatPresetService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ChatPresetDto> update(@PathVariable Long id,
                                                @Valid @RequestBody UpdatePresetRequest request) {
        return ResponseEntity.ok(chatPresetService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        chatPresetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
