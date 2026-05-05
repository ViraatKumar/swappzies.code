package com.swapper.monolith.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="\"user\"",indexes = {
        @Index(name="idx_user_id",columnList = "user_id"),
        @Index(name="idx_username",columnList = "username"),
        @Index(name="idx_email",columnList = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="user_id",unique = true)
    String userId;
    @JsonIgnore
    String password;
    @Column(unique = true)
    String username;
    String email;
    @Column(name="phone_no")
    String phoneNo;

    @Column(name = "display_name")
    String displayName;

    @Column(columnDefinition = "TEXT")
    String bio;

    @Column(name = "avatar_url")
    String avatarUrl;

    Double lat;

    Double lng;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Roles> roles;

    @CreationTimestamp
    @Column(name="created_at")
    Instant createdAt;

    @UpdateTimestamp
    @Column(name="updated_at")
    Instant updatedAt;

    @Version
    @Column(name="version",nullable = false)
    Long version;

}
