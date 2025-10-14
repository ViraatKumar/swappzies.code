package com.swapper.monolith.TradeService.dto;

import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user_order")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="order_id",unique = true,nullable = false)
    private String orderId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="buyer_id",referencedColumnName = "user_id")
    private User buyer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="seller_id",referencedColumnName = "user_id")
    private User seller;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="game_id",referencedColumnName = "game_id")
    private Game game;
}
