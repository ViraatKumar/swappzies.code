package com.swapper.monolith.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swapper.monolith.dto.UserDTO;
import com.swapper.monolith.dto.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
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

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Roles> roles;


}
