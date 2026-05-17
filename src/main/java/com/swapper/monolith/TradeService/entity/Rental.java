package com.swapper.monolith.TradeService.entity;

import com.swapper.monolith.ItemService.entity.UserGamePost;
import com.swapper.monolith.TradeService.dto.constant.RentalStatus;
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
    name = "rental",
    indexes = {
        @Index(name = "idx_rental_renter", columnList = "renter_id"),
        @Index(name = "idx_rental_owner", columnList = "owner_id")
    }
)
public class Rental extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rental_id", unique = true, nullable = false, updatable = false)
    private String rentalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "renter_id", nullable = false)
    private User renter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private UserGamePost listing;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalStatus status;

    @Column(name = "rental_start_date")
    private Instant rentalStartDate;

    @Column(name = "rental_end_date")
    private Instant rentalEndDate;

    @Column(name = "returned_at")
    private Instant returnedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
