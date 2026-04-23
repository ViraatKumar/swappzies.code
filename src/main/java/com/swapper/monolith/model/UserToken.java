package com.swapper.monolith.model;

import com.swapper.monolith.model.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_token")
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(unique = true, nullable = false)
    String token; // SHA-256 hash of the raw token

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TokenType type;

    @Column(name = "expiry_date", nullable = false)
    Instant expiryDate;

    @Column(name = "created_at", nullable = false)
    Instant createdAt;

    @Column(nullable = false)
    boolean used; // only checked for PASSWORD_RESET tokens
}
