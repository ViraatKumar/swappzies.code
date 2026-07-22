package com.swapper.monolith.ChatService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swapper.monolith.ChatService.dtos.MessageDto;
import com.swapper.monolith.ChatService.dtos.SendMessageDto;
import com.swapper.monolith.ChatService.dtos.enums.MessageType;
import com.swapper.monolith.ChatService.service.ChatService;
import com.swapper.monolith.exception.CustomExceptions.DuplicatedResourceException;
import com.swapper.monolith.exception.CustomExceptions.ForbiddenException;
import com.swapper.monolith.exception.CustomExceptions.ResourceNotFoundException;
import com.swapper.monolith.exception.GlobalExceptionHandler;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.service.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatController chatController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserDetailsImpl principal;

    private static final String USER_ID = "user-123";
    private static final String USERNAME = "alice";
    private static final String CONVERSATION_ID = "conv-abc";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        MappingJackson2HttpMessageConverter jsonConverter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(chatController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(jsonConverter)
                .build();

        principal = new UserDetailsImpl(
                USER_ID,
                USERNAME,
                "hashed-password",
                List.of(new SimpleGrantedAuthority("USER")));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, "hashed-password", principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // -------------------------- POST /chat/v1/messages --------------------------

    @Test
    void sendMessageRest_plainText_returnsSavedMessageAndBroadcasts() throws Exception {
        SendMessageDto request = plainTextRequest("hello there");
        MessageDto saved = plainTextResponse(42L, "hello there");

        when(chatService.sendMessage(any(SendMessageDto.class), eq(principal))).thenReturn(saved);

        mockMvc.perform(post("/chat/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.conversation_id").value(CONVERSATION_ID))
                .andExpect(jsonPath("$.sender_id").value(USER_ID))
                .andExpect(jsonPath("$.sender_username").value(USERNAME))
                .andExpect(jsonPath("$.message_type").value("PLAIN_TEXT"))
                .andExpect(jsonPath("$.content").value("hello there"));

        ArgumentCaptor<SendMessageDto> requestCaptor = ArgumentCaptor.forClass(SendMessageDto.class);
        verify(chatService).sendMessage(requestCaptor.capture(), eq(principal));
        SendMessageDto delivered = requestCaptor.getValue();
        assertThat(delivered.getConversationId()).isEqualTo(CONVERSATION_ID);
        assertThat(delivered.getMessageType()).isEqualTo(MessageType.PLAIN_TEXT);
        assertThat(delivered.getContent()).isEqualTo("hello there");

        verify(messagingTemplate).convertAndSend(
                "/topic/conversation." + CONVERSATION_ID,
                saved);
    }

    @Test
    void sendMessageRest_forbidden_returns403AndDoesNotBroadcast() throws Exception {
        SendMessageDto request = plainTextRequest("hi");
        when(chatService.sendMessage(any(SendMessageDto.class), any(UserDetailsImpl.class)))
                .thenThrow(new ForbiddenException(ApiResponses.CHAT_NOT_A_PARTICIPANT.getMessage()));

        mockMvc.perform(post("/chat/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value(ApiResponses.CHAT_NOT_A_PARTICIPANT.getMessage()));

        verify(messagingTemplate, never()).convertAndSend(any(String.class), any(Object.class));
    }

    @Test
    void sendMessageRest_conversationNotFound_returns404() throws Exception {
        SendMessageDto request = plainTextRequest("hi");
        when(chatService.sendMessage(any(SendMessageDto.class), any(UserDetailsImpl.class)))
                .thenThrow(new ResourceNotFoundException(
                        ApiResponses.CHAT_CONVERSATION_NOT_FOUND.getMessage()));

        mockMvc.perform(post("/chat/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(ApiResponses.CHAT_CONVERSATION_NOT_FOUND.getMessage()));

        verify(messagingTemplate, never()).convertAndSend(any(String.class), any(Object.class));
    }

    @Test
    void sendMessageRest_presetInactive_returns409() throws Exception {
        SendMessageDto request = new SendMessageDto();
        request.setConversationId(CONVERSATION_ID);
        request.setMessageType(MessageType.PRESET);
        request.setPresetId(9L);

        when(chatService.sendMessage(any(SendMessageDto.class), any(UserDetailsImpl.class)))
                .thenThrow(new DuplicatedResourceException(ApiResponses.CHAT_PRESET_INACTIVE));

        mockMvc.perform(post("/chat/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(ApiResponses.CHAT_PRESET_INACTIVE.getMessage()));

        verify(messagingTemplate, never()).convertAndSend(any(String.class), any(Object.class));
    }

    @Test
    void sendMessageRest_invalidTypeFields_returns400() throws Exception {
        SendMessageDto request = new SendMessageDto();
        request.setConversationId(CONVERSATION_ID);
        request.setMessageType(MessageType.PLAIN_TEXT);
        request.setPresetId(1L);

        when(chatService.sendMessage(any(SendMessageDto.class), any(UserDetailsImpl.class)))
                .thenThrow(new DuplicatedResourceException(
                        ApiResponses.CHAT_INVALID_MESSAGE_TYPE_FIELDS));

        mockMvc.perform(post("/chat/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ApiResponses.CHAT_INVALID_MESSAGE_TYPE_FIELDS.getMessage()));

        verify(messagingTemplate, never()).convertAndSend(any(String.class), any(Object.class));
    }

    // ---------------- GET /chat/v1/conversations/{conversationId}/messages ----------------

    @Test
    void getMessages_defaultPaging_returnsPageAndUsesDefaults() throws Exception {
        MessageDto message = plainTextResponse(1L, "old message");
        Page<MessageDto> page = new PageImpl<>(
                List.of(message),
                PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "createdAt")),
                1);

        when(chatService.getMessages(eq(CONVERSATION_ID), eq(0), eq(30), eq(principal)))
                .thenReturn(page);

        mockMvc.perform(get("/chat/v1/conversations/{id}/messages", CONVERSATION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].conversation_id").value(CONVERSATION_ID))
                .andExpect(jsonPath("$.content[0].content").value("old message"));

        verify(chatService).getMessages(CONVERSATION_ID, 0, 30, principal);
    }

    @Test
    void getMessages_customPaging_passesThroughParameters() throws Exception {
        Page<MessageDto> page = new PageImpl<>(
                List.of(),
                PageRequest.of(2, 5, Sort.by(Sort.Direction.DESC, "createdAt")),
                12);

        when(chatService.getMessages(eq(CONVERSATION_ID), eq(2), eq(5), eq(principal)))
                .thenReturn(page);

        mockMvc.perform(get("/chat/v1/conversations/{id}/messages", CONVERSATION_ID)
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(0)));

        verify(chatService).getMessages(CONVERSATION_ID, 2, 5, principal);
    }

    @Test
    void getMessages_forbidden_returns403() throws Exception {
        when(chatService.getMessages(eq(CONVERSATION_ID), eq(0), eq(30), any(UserDetailsImpl.class)))
                .thenThrow(new ForbiddenException(ApiResponses.CHAT_NOT_A_PARTICIPANT.getMessage()));

        mockMvc.perform(get("/chat/v1/conversations/{id}/messages", CONVERSATION_ID))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value(ApiResponses.CHAT_NOT_A_PARTICIPANT.getMessage()));
    }

    @Test
    void getMessages_notFound_returns404() throws Exception {
        when(chatService.getMessages(eq(CONVERSATION_ID), eq(0), eq(30), any(UserDetailsImpl.class)))
                .thenThrow(new ResourceNotFoundException(
                        ApiResponses.CHAT_CONVERSATION_NOT_FOUND.getMessage()));

        mockMvc.perform(get("/chat/v1/conversations/{id}/messages", CONVERSATION_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(ApiResponses.CHAT_CONVERSATION_NOT_FOUND.getMessage()));
    }

    // -------------------------- WebSocket @MessageMapping --------------------------

    @Test
    void sendMessageOverWebSocket_authenticatedPrincipal_savesAndBroadcasts() {
        SendMessageDto request = plainTextRequest("ws hi");
        MessageDto saved = plainTextResponse(7L, "ws hi");

        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());

        when(chatService.sendMessage(request, principal)).thenReturn(saved);

        chatController.sendMessageOverWebSocket(request, auth);

        verify(chatService).sendMessage(request, principal);
        verify(messagingTemplate).convertAndSend(
                "/topic/conversation." + CONVERSATION_ID,
                saved);
    }

    @Test
    void sendMessageOverWebSocket_principalNotAuthentication_throwsAndDoesNothing() {
        SendMessageDto request = plainTextRequest("ws hi");
        Principal rawPrincipal = () -> "raw-name";

        assertThatThrownBy(() -> chatController.sendMessageOverWebSocket(request, rawPrincipal))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("WebSocket principal is not authenticated");

        verifyNoInteractions(chatService);
        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void sendMessageOverWebSocket_authWithNonUserDetailsPrincipal_throwsAndDoesNothing() {
        SendMessageDto request = plainTextRequest("ws hi");
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "not-a-user-details", null, List.of(new SimpleGrantedAuthority("USER")));

        assertThatThrownBy(() -> chatController.sendMessageOverWebSocket(request, auth))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("WebSocket principal is not authenticated");

        verifyNoInteractions(chatService);
        verifyNoInteractions(messagingTemplate);
    }

    // -------------------------- helpers --------------------------

    private SendMessageDto plainTextRequest(String content) {
        SendMessageDto req = new SendMessageDto();
        req.setConversationId(CONVERSATION_ID);
        req.setMessageType(MessageType.PLAIN_TEXT);
        req.setContent(content);
        return req;
    }

    private MessageDto plainTextResponse(Long id, String content) {
        return MessageDto.builder()
                .id(id)
                .conversationId(CONVERSATION_ID)
                .senderId(USER_ID)
                .senderUsername(USERNAME)
                .messageType(MessageType.PLAIN_TEXT)
                .content(content)
                .createdAt(Instant.parse("2026-01-15T10:00:00Z"))
                .build();
    }
}
