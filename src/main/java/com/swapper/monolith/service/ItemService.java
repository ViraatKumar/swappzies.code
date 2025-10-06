package com.swapper.monolith.service;

import com.swapper.monolith.dto.ApiResponse;
import com.swapper.monolith.dto.CreateItemRequest;
import com.swapper.monolith.dto.UserDTO;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.Item;
import com.swapper.monolith.model.ItemImage;
import com.swapper.monolith.model.User;
import com.swapper.monolith.repository.ItemRepository;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ItemService {
    ItemRepository itemRepository;
    UserService userService;
    public ApiResponse<Item> createItem(CreateItemRequest createItemRequest) {
        Item newItem = Item.from(createItemRequest);
        List<ItemImage> itemImages = createItemRequest.imageUrls().stream().map(imageUrl->
                ItemImage.builder()
                        .imageUrl(imageUrl)
                        .item(newItem)
                        .build()

        ).toList();
        newItem.setImages(itemImages);

        User user = userService.findByUserId(createItemRequest.userId());
        if (user == null) {
            LoggerFactory.getLogger(ItemService.class).error("User with user id - " + createItemRequest.userId() + " not found");
            throw new UsernameNotFoundException("User not found");
        }
        newItem.setOwner(user);
        itemRepository.save(newItem);

        ApiResponses response = ApiResponses.CREATED;
        return ApiResponse.<Item>builder()
                .status(response.getHttpStatus())
                .payload(newItem)
                .message(response.getMessage())
                .build();
    }
    public List<Item> getUsersItems(String userId){
        return null;
    }
}
