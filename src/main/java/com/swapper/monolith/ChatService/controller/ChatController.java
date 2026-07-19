package com.swapper.monolith.ChatService.controller;

import com.swapper.monolith.ChatService.dtos.MessageDto;
import com.swapper.monolith.ChatService.dtos.SendMessageDto;
import com.swapper.monolith.ChatService.service.ChatService;
import com.swapper.monolith.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/chat/v1")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER')")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/messages")
    public ResponseEntity<MessageDto> sendMessageRest(@Valid @RequestBody SendMessageDto request) {
        MessageDto saved = chatService.sendMessage(request, getRestPrincipal());
        broadcast(saved);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Page<MessageDto>> getMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        return ResponseEntity.ok(chatService.getMessages(conversationId, page, size, getRestPrincipal()));
    }

    @MessageMapping("/chat.send")
    public void sendMessageOverWebSocket(@Payload SendMessageDto request, Principal principal) {
        UserDetailsImpl userDetails = extractWebSocketPrincipal(principal);
        MessageDto saved = chatService.sendMessage(request, userDetails);
        broadcast(saved);
    }

    private void broadcast(MessageDto message) {
        messagingTemplate.convertAndSend("/topic/conversation." + message.getConversationId(), message);
    }

    private UserDetailsImpl getRestPrincipal() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private UserDetailsImpl extractWebSocketPrincipal(Principal principal) {
        if (principal instanceof Authentication auth && auth.getPrincipal() instanceof UserDetailsImpl impl) {
            return impl;
        }
        throw new IllegalStateException("WebSocket principal is not authenticated");
    }
}
