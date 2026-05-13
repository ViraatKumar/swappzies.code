package com.swapper.monolith.WishlistService.entity;

import com.swapper.monolith.ItemService.entity.GameEntity;
import com.swapper.monolith.model.BaseModel;
import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
    name = "wishlist_item",
    indexes = {
        @Index(name = "idx_wishlist_user_id", columnList = "user_id"),
        @Index(name = "idx_wishlist_user_game", columnList = "user_id, game_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_wishlist_user_game", columnNames = {"user_id", "game_id"})
    }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WishlistItem extends BaseModel {

    @Id
    @Column(name = "wishlist_item_id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    String wishlistItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    GameEntity game;

}
