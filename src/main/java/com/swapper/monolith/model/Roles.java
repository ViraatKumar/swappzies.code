package com.swapper.monolith.model;

import com.swapper.monolith.dto.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="roles")
public class Roles {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private Role role;
}
