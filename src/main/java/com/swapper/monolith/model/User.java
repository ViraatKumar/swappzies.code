package com.swapper.monolith.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swapper.monolith.dto.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    String userId;
    @Column(nullable = false)
    String username;
    String password;
    String email;
    String phoneNo;
    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Roles> roles;
}
