package com.swapper.monolith.ItemService.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class IgdbGameDto {

    private Long id;
    private String name;
    private String slug;
    private String summary;
    private String storyline;
    private String url;
    @JsonProperty("age_ratings")
    private List<Long> ageRatings;

    @JsonProperty("aggregated_rating")
    private Double aggregatedRating;

    @JsonProperty("aggregated_rating_count")
    private Integer aggregatedRatingCount;

    @JsonProperty("alternative_names")
    private List<Long> alternativeNames;

    private List<Long> artworks;
    private List<Long> bundles;

    /** DEPRECATED – mapped for backward compatibility */
    private GameCategory category;

    private UUID checksum;

    /** DEPRECATED */
    private Long collection;

    private List<Long> collections;
    private Long cover;

    @JsonProperty("created_at")
    private Instant createdAt;

    private List<Long> dlcs;

    @JsonProperty("expanded_games")
    private List<Long> expandedGames;

    private List<Long> expansions;

    @JsonProperty("external_games")
    private List<Long> externalGames;

    @JsonProperty("first_release_date")
    private Long firstReleaseDate; // Unix timestamp

    private List<Long> forks;
    private Long franchise;
    private List<Long> franchises;

    @JsonProperty("game_engines")
    private List<Long> gameEngines;

    @JsonProperty("game_localizations")
    private List<Long> gameLocalizations;

    @JsonProperty("game_modes")
    private List<Long> gameModes;

    @JsonProperty("game_status")
    private GameStatus gameStatus;

    @JsonProperty("game_type")
    private GameCategory gameType;

    private List<Long> genres;
    private Integer hypes;

    @JsonProperty("involved_companies")
    private List<Long> involvedCompanies;

    private List<Long> keywords;

    @JsonProperty("language_supports")
    private List<Long> languageSupports;

    @JsonProperty("multiplayer_modes")
    private List<Long> multiplayerModes;

    @JsonProperty("parent_game")
    private Long parentGame;

    private List<Long> platforms;

    @JsonProperty("player_perspectives")
    private List<Long> playerPerspectives;

    private List<Long> ports;

    private Double rating;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("release_dates")
    private List<Long> releaseDates;

    private List<Long> remakes;
    private List<Long> remasters;
    private List<Long> screenshots;

    @JsonProperty("similar_games")
    private List<Long> similarGames;

    @JsonProperty("standalone_expansions")
    private List<Long> standaloneExpansions;

    /** DEPRECATED */
    private GameStatus status;

    private List<Long> tags;
    private List<Long> themes;

    @JsonProperty("total_rating")
    private Double totalRating;

    @JsonProperty("total_rating_count")
    private Integer totalRatingCount;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    @JsonProperty("version_parent")
    private Long versionParent;

    @JsonProperty("version_title")
    private String versionTitle;

    private List<Long> videos;
    private List<Long> websites;

    // getters & setters (or Lombok @Data)
}
