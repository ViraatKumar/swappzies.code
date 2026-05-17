package com.swapper.monolith.TradeService.entity;

import com.swapper.monolith.ItemService.entity.UserGamePost;
import com.swapper.monolith.TradeService.dto.constant.TradeStatus;
import com.swapper.monolith.model.BaseModel;
import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
    name = "trade",
    indexes = {
        @Index(name = "idx_trade_initiator", columnList = "initiator_id"),
        @Index(name = "idx_trade_receiver", columnList = "receiver_id"),
        @Index(name = "idx_trade_status", columnList = "status")
    }
)
public class Trade extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "trade_id", unique = true, nullable = false, updatable = false)
    private String tradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offered_listing_id", nullable = false)
    private UserGamePost offeredListing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_listing_id", nullable = false)
    private UserGamePost requestedListing;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TradeStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "completed_at")
    private Instant completedAt;
}
