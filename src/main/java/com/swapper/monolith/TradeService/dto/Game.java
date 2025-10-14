package com.swapper.monolith.TradeService.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swapper.monolith.dto.CreateItemRequest;
import com.swapper.monolith.TradeService.dto.enums.Condition;
import com.swapper.monolith.TradeService.dto.enums.Console;
import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="game")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="game_id",unique = true)
    private String gameId;

    @Enumerated(EnumType.STRING)
    private Console console;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="item_condition")
    private Condition condition;

    @Column
    private Double price;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id",referencedColumnName = "user_id",columnDefinition = "CHAR(36)")
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemImage> images;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name="published", nullable = false)
    @Builder.Default
    private boolean published = false;

    public static Game from(CreateItemRequest createItemRequest) {

        return Game.builder()
                .title(createItemRequest.title())
                .description(createItemRequest.description())
                .price(createItemRequest.price())
                .console(createItemRequest.console())
                .condition(createItemRequest.condition())
                .build();
    }
}

