package com.swapper.monolith.ChatService.entity;

import com.swapper.monolith.model.BaseModel;
import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(
        name = "conversation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conversation_participants",
                        columnNames = {"participant_a_id", "participant_b_id"}
                )
        },
        indexes = {
                @Index(name = "idx_conversation_id", columnList = "conversation_id"),
                @Index(name = "idx_conversation_participant_a", columnList = "participant_a_id"),
                @Index(name = "idx_conversation_participant_b", columnList = "participant_b_id"),
                @Index(name = "idx_conversation_last_message_at", columnList = "last_message_at")
        }
)
public class Conversation extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "conversation_id", unique = true, nullable = false, updatable = false)
    private String conversationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_a_id", nullable = false, updatable = false)
    private User participantA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_b_id", nullable = false, updatable = false)
    private User participantB;

    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    @Column(name = "last_message_preview", length = 280)
    private String lastMessagePreview;
}
