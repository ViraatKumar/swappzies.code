package com.swapper.monolith.ItemService.dto.UserGamePost;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swapper.monolith.ItemService.constants.Condition;
import com.swapper.monolith.ItemService.constants.ItemStatus;
import com.swapper.monolith.ItemService.constants.ListingState;
import com.swapper.monolith.ItemService.constants.OfferType;
import com.swapper.monolith.ItemService.constants.Platform;
import com.swapper.monolith.ItemService.dto.GameDto;
import com.swapper.monolith.ItemService.entity.UserGamePost;
import com.swapper.monolith.ItemService.mapper.GameMapper;
import com.swapper.monolith.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class UserGamePostDto {
    @JsonIgnore
    UserDTO user;
    @JsonIgnore
    GameDto game;

    String listingId;
    String username;
    String gameName;
    Date postDate;
    String coverUrl;
    Condition condition;
    Platform platform;
    ItemStatus itemStatus;
    List<OfferType> offerTypes;
    String description;
    int featuredPriority;
    ListingState listingState;
    public static UserGamePostDto from(UserGamePost userGamePost, String coverUrl) {
        GameMapper gameMapper = new GameMapper();
        GameDto game = gameMapper.toDto(userGamePost.getGame());
        UserDTO user = UserDTO.from(userGamePost.getUser());
        return UserGamePostDto.builder()
                .listingId(userGamePost.getListingId())
                .user(user)
                .game(game)
                .username(user.getUsername())
                .gameName(game.getName())
                .postDate(userGamePost.getCreatedDate())
                .coverUrl(coverUrl)
                .platform(userGamePost.getPlatform())
                .condition(userGamePost.getCondition())
                .itemStatus(userGamePost.getItemStatus())
                .offerTypes(userGamePost.getOfferTypes())
                .description(userGamePost.getDescription())
                .featuredPriority(userGamePost.getFeaturedPriority())
                .listingState(userGamePost.getListingState())
                .build();
    }
}
