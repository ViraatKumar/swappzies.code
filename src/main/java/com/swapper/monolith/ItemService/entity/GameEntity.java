package com.swapper.monolith.ItemService.entity;

import com.swapper.monolith.ItemService.constants.Condition;
import com.swapper.monolith.ItemService.constants.Console;
import com.swapper.monolith.ItemService.constants.ItemStatus;
import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@FieldDefaults(level= AccessLevel.PRIVATE)
@Table(name="game")
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="game_id")
    @Setter(AccessLevel.NONE)
    String gameId;

    @Column
    String name;

    @Column
    @Enumerated(EnumType.STRING)
    Condition condition;

    @Column
    @Enumerated(EnumType.STRING)
    Console console;

    @Column
    String region;

    @Column
    BigDecimal price;

    @Enumerated(EnumType.STRING)
    ItemStatus status;

    @JoinColumn(name="user_id", nullable = false,referencedColumnName = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    User owner;

    @Column(updatable = false,name="created_at")
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    Date createdAt;

    @Column(name="updated_at")
    @UpdateTimestamp
    Date updatedAt;
}
