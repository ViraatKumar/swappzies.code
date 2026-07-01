package com.swapper.monolith.ChatService.entity;

import com.swapper.monolith.ChatService.dtos.enums.MessageType;
import com.swapper.monolith.model.BaseModel;
import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(
        name = "chat_message",
        indexes = {
                @Index(name = "idx_chat_message_conversation", columnList = "conversation_id, created_at"),
                @Index(name = "idx_chat_message_sender", columnList = "sender_id"),
                @Index(name = "idx_chat_message_created_at", columnList = "created_at")
        }
)
public class ChatMessage extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false, updatable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false, updatable = false, referencedColumnName = "user_id")
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 32)
    private MessageType messageType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "preset_id")
    private Long presetId;

    @Column(name = "meetup_at")
    private Instant meetupAt;

    @Column(name = "location", length = 280)
    private String location;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
