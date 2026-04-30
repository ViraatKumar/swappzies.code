package com.swapper.monolith.ItemService.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swapper.monolith.ItemService.constants.Condition;
import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name="user_game")
@FieldDefaults(level= AccessLevel.PRIVATE)
public class UserGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String userId;
    String gameId;
    String condition;
}
