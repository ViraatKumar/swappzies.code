package com.swapper.monolith.ItemService.entity;

import com.swapper.monolith.ItemService.constants.*;
import com.swapper.monolith.model.BaseModel;
import com.swapper.monolith.model.User;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.DomainEvents;

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
    @Id
    @Column(name = "listing_id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    String listingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    GameEntity game;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    Platform platform;

    @Enumerated(EnumType.STRING)
    Condition condition;

    @Enumerated(EnumType.STRING)
    ItemStatus itemStatus;

    @Type(JsonBinaryType.class)
    @Column(name="offer_types",columnDefinition = "jsonb")
    List<OfferType> offerTypes = new ArrayList<>();

    Double price;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "featured_priority", nullable = false)
    int featuredPriority = 0;

    @Column(name = "deleted_at")
    Instant deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(name="listing_state", nullable = false)
    ListingState listingState;
//
//    @DomainEvents
//    public void domainOperation(){
//
//    }

}
