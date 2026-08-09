package com.swapper.monolith.VaultService.entity;

import com.swapper.monolith.ItemService.constants.Platform;
import com.swapper.monolith.ItemService.entity.GameEntity;
import com.swapper.monolith.VaultService.dto.constant.CompletionStatus;
import com.swapper.monolith.model.BaseModel;
import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(
        name="user_game",
        indexes = {
                @Index(name="idx_user_game_id",columnList = "userGameId"),
                @Index(name="idx_user_owned_games",columnList = "user_id")
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserGame extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="user_game_id",unique = true,nullable = false,updatable = false)
    String userGameId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    User user;

    @OneToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="game_id",nullable=false,referencedColumnName = "id")
    GameEntity game;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    Platform platform;

    @Column(name = "favorite")
    Boolean favorite = false;

    @Enumerated(EnumType.STRING)
    CompletionStatus completionStatus = CompletionStatus.NOT_STARTED;

    @Column(name="cover_url")
    String coverUrl;
}
