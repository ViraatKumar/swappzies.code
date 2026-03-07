package com.swapper.monolith.ItemService.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swapper.monolith.ItemService.dto.GameCategory;
import com.swapper.monolith.ItemService.dto.GameStatus;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "game")
public class GameEntity {

    @Id
    private Long id;
    @Column(name="name")
    private String name;
    @Column(name="slug")
    private String slug;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(columnDefinition = "text")
    private String storyline;

    private String url;

    @Type(JsonBinaryType.class)
    @Column(name = "age_ratings", columnDefinition = "jsonb")
    private List<Long> ageRatings;

    @Column(name = "aggregated_rating")
    private Double aggregatedRating;

    @Column(name = "aggregated_rating_count")
    private Integer aggregatedRatingCount;

    @Type(JsonBinaryType.class)
    @Column(name = "alternative_names", columnDefinition = "jsonb")
    private List<Long> alternativeNames;

    @Type(JsonBinaryType.class)
    @Column(name = "artworks", columnDefinition = "jsonb")
    private List<Long> artworks;

    @Type(JsonBinaryType.class)
    @Column(name = "bundles", columnDefinition = "jsonb") // Fixed typo 'bundgles'
    private List<Long> bundles;

    private UUID checksum;

    @Type(JsonBinaryType.class)
    @Column(name = "collections", columnDefinition = "jsonb")
    private List<Long> collections;

    @Column(name = "cover")
    private Long cover;

    @Column(name = "created_at")
    private Instant createdAt;

    @Type(JsonBinaryType.class)
    @Column(name = "dlcs", columnDefinition = "jsonb")
    private List<Long> dlcs;

    @Type(JsonBinaryType.class)
    @Column(name = "expanded_games", columnDefinition = "jsonb")
    private List<Long> expandedGames;

    @Type(JsonBinaryType.class)
    @Column(name = "expansions", columnDefinition = "jsonb")
    private List<Long> expansions;

    @Type(JsonBinaryType.class)
    @Column(name = "external_games", columnDefinition = "jsonb")
    private List<Long> externalGames;

    @Column(name = "first_release_date")
    private Long firstReleaseDate;

    @Type(JsonBinaryType.class)
    @Column(name = "forks", columnDefinition = "jsonb") // Fixed columnDefinition typo
    private List<Long> forks;

    @Column(name = "franchise")
    private Long franchise;

    @Type(JsonBinaryType.class)
    @Column(name = "franchises", columnDefinition = "jsonb")
    private List<Long> franchises;

    @Type(JsonBinaryType.class)
    @Column(name = "game_engines", columnDefinition = "jsonb")
    private List<Long> gameEngines;

    @Type(JsonBinaryType.class)
    @Column(name = "game_localizations", columnDefinition = "jsonb")
    private List<Long> gameLocalizations;

    @Type(JsonBinaryType.class)
    @Column(name = "game_modes", columnDefinition = "jsonb")
    private List<Long> gameModes;

    @Column(name = "game_status")
    private String gameStatus;

    @JsonProperty("game_type")
    private String gameType;

    @Type(JsonBinaryType.class)
    @Column(name = "genres", columnDefinition = "jsonb")
    private List<Long> genres;

    @Column(name = "hypes")
    private Integer hypes;

    @Type(JsonBinaryType.class)
    @Column(name = "involved_companies", columnDefinition = "jsonb")
    private List<Long> involvedCompanies;

    @Type(JsonBinaryType.class)
    @Column(name = "keywords", columnDefinition = "jsonb")
    private List<Long> keywords;

    @Type(JsonBinaryType.class)
    @Column(name = "language_supports", columnDefinition = "jsonb")
    private List<Long> languageSupports;

    @Type(JsonBinaryType.class)
    @Column(name = "multiplayer_modes", columnDefinition = "jsonb")
    private List<Long> multiplayerModes;

    @Column(name = "parent_game")
    private Long parentGame;

    @Type(JsonBinaryType.class)
    @Column(name = "platforms", columnDefinition = "jsonb")
    private List<Long> platforms;

    @Type(JsonBinaryType.class)
    @Column(name = "player_perspectives", columnDefinition = "jsonb")
    private List<Long> playerPerspectives;

    @Type(JsonBinaryType.class)
    @Column(name = "ports", columnDefinition = "jsonb")
    private List<Long> ports;

    private Double rating;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @Type(JsonBinaryType.class)
    @Column(name = "release_dates", columnDefinition = "jsonb")
    private List<Long> releaseDates;

    @Type(JsonBinaryType.class)
    @Column(name = "remakes", columnDefinition = "jsonb")
    private List<Long> remakes;

    @Type(JsonBinaryType.class)
    @Column(name = "remasters", columnDefinition = "jsonb")
    private List<Long> remasters;

    @Type(JsonBinaryType.class)
    @Column(name = "screenshots", columnDefinition = "jsonb")
    private List<Long> screenshots;

    @Type(JsonBinaryType.class)
    @Column(name = "similar_games", columnDefinition = "jsonb")
    private List<Long> similarGames;

    @Type(JsonBinaryType.class)
    @Column(name = "standalone_expansions", columnDefinition = "jsonb")
    private List<Long> standaloneExpansions;

    @Type(JsonBinaryType.class)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<Long> tags;

    @Type(JsonBinaryType.class)
    @Column(name = "themes", columnDefinition = "jsonb")
    private List<Long> themes;

    @Column(name = "total_rating")
    private Double totalRating;

    @Column(name = "total_rating_count")
    private Integer totalRatingCount;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "version_parent")
    private Long versionParent;

    @Column(name = "version_title")
    private String versionTitle;

    @Type(JsonBinaryType.class)
    @Column(name = "videos", columnDefinition = "jsonb")
    private List<Long> videos;

    @Type(JsonBinaryType.class)
    @Column(name = "websites", columnDefinition = "jsonb")
    private List<Long> websites;
}