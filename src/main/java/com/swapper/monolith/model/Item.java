package com.swapper.monolith.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swapper.monolith.dto.CreateItemRequest;
import com.swapper.monolith.dto.enums.Condition;
import com.swapper.monolith.dto.enums.Console;
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
@Table(name="item")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    public static Item from(CreateItemRequest createItemRequest) {

        return Item.builder()
                .title(createItemRequest.title())
                .description(createItemRequest.description())
                .price(createItemRequest.price())
                .console(createItemRequest.console())
                .condition(createItemRequest.condition())
                .build();
    }
}

