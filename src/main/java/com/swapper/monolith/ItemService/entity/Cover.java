package com.swapper.monolith.ItemService.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cover")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cover {

    @Id
    Long id;

    @Column(name = "alpha_channel")
    Boolean alphaChannel;

    Boolean animated;

    UUID checksum;

    Long game;

    @Column(name = "game_localization")
    Long gameLocalization;

    Integer height;

    @Column(name = "image_id")
    String imageId;

    String url;

    Integer width;
}
