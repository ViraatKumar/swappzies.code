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
@Table(name="\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="user_id",unique = true)
    String userId;
    @JsonIgnore
    String password;
    @Column(unique = true)
    String username;
    String email;
    @Column(name="hello")
    String phoneNo;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Roles> roles;


}
