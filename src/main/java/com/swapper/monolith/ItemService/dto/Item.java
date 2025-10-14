package com.swapper.monolith.ItemService.dto;

import com.swapper.monolith.TradeService.dto.enums.Condition;
import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String itemId;

    @Column
    String title;

    @Column
    String description;

    @Column
    @Enumerated(EnumType.STRING)
    Condition condition;

    @Column
    double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",referencedColumnName = "user_id")
    User owner;


}
