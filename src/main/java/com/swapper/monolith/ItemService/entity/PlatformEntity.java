package com.swapper.monolith.ItemService.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "platform")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlatformEntity {

    @Id
    Long id;

    String abbreviation;

    @Column(columnDefinition = "text")
    String alternativeName;

    String checksum;

    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt;

    Integer generation;

    String name;

    Long platformFamily;

    Long platformLogo;

    Long platformType;

    String slug;

    @Column(columnDefinition = "text")
    String summary;

    @Temporal(TemporalType.TIMESTAMP)
    Date updatedAt;

    @Column(columnDefinition = "text")
    String url;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    List<Long> versions;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    List<Long> websites;
}
