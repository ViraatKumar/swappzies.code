package com.swapper.monolith.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name="genre")
@FieldDefaults(level= AccessLevel.PRIVATE)
public class GenreEntity {
    @Id
    Long id;

    String name;

    String checksum;

    String slug;

    String url;
}