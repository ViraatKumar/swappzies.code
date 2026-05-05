package com.swapper.monolith.ItemService.entity;

import com.swapper.monolith.ItemService.constants.*;
import com.swapper.monolith.model.BaseModel;
import com.swapper.monolith.model.User;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
    name = "user_game_post",
    indexes = {
        @Index(name = "idx_ugp_user_id_listing_state", columnList = "user_id, listing_state")
    }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserGamePost extends BaseModel {
    @EmbeddedId
    UserGamePostId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("gameId")
    @JoinColumn(name = "game_id", nullable = false)
    GameEntity game;

    @Enumerated(EnumType.STRING)
    Condition condition;

    @Enumerated(EnumType.STRING)
    ItemStatus itemStatus;

    @Type(JsonBinaryType.class)
    @Column(name="offer_types",columnDefinition = "jsonb")
    List<OfferType> offerTypes = new ArrayList<>();

    Double price;

    @Column(name = "listing_id", unique = true, nullable = false, updatable = false)
    @UuidGenerator
    String listingId;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "featured_priority", nullable = false)
    int featuredPriority = 0;

    @Column(name = "deleted_at")
    Instant deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(name="listing_state", nullable = false)
    ListingState listingState;

}
