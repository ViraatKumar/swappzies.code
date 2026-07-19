package com.swapper.monolith.ChatService.controller;

import com.swapper.monolith.ChatService.dtos.ConversationDto;
import com.swapper.monolith.ChatService.dtos.MarkReadRequest;
import com.swapper.monolith.ChatService.service.ConversationService;
import com.swapper.monolith.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/v1/conversations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER')")
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    public ResponseEntity<List<ConversationDto>> listConversations() {
        return ResponseEntity.ok(conversationService.listConversations(getPrincipal()));
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationDto> getConversation(@PathVariable String conversationId) {
        return ResponseEntity.ok(conversationService.getConversation(conversationId, getPrincipal()));
    }

    @PostMapping("/{conversationId}/read")
    public ResponseEntity<Void> markRead(@PathVariable String conversationId,
                                         @Valid @RequestBody MarkReadRequest request) {
        conversationService.markRead(conversationId, request.getLastReadMessageId(), getPrincipal());
        return ResponseEntity.noContent().build();
    }

    private UserDetailsImpl getPrincipal() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
