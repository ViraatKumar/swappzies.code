package com.swapper.monolith.event;


import com.swapper.monolith.ItemService.dto.UserGamePost.UserGamePostDto;
import com.swapper.monolith.dto.UserDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.DomainEvents;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewListingEvent {
    UserDTO user;
    UserGamePostDto userGamePost;
    public NewListingEvent(UserDTO user, UserGamePostDto userGamePost) {
        this.user = user;
        this.userGamePost = userGamePost;
    }
}
