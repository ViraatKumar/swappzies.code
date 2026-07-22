package com.swapper.monolith.WishlistService.service;

import com.swapper.monolith.ItemService.entity.GameEntity;
import com.swapper.monolith.ItemService.service.CoverService;
import com.swapper.monolith.ItemService.service.GameService;
import com.swapper.monolith.WishlistService.dto.AddToWishlistRequest;
import com.swapper.monolith.WishlistService.dto.WishlistItemDto;
import com.swapper.monolith.WishlistService.entity.WishlistItem;
import com.swapper.monolith.WishlistService.repository.WishlistRepository;
import com.swapper.monolith.exception.CustomExceptions.DuplicatedResourceException;
import com.swapper.monolith.exception.CustomExceptions.ForbiddenException;
import com.swapper.monolith.exception.CustomExceptions.InternalServerException;
import com.swapper.monolith.exception.CustomExceptions.ResourceNotFoundException;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.User;
import com.swapper.monolith.service.UserDetailsImpl;
import com.swapper.monolith.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private static final int MAX_WISHLIST_ITEMS = 10;
    private final WishlistRepository wishlistRepository;
    private final GameService gameService;
    private final UserService userService;
    private final CoverService coverService;

    public List<WishlistItemDto> getWishlist(UserDetailsImpl principal) {
        List<WishlistItem> wishlistItems = wishlistRepository.findByUserId(principal.getUserId());

        List<Long> coverIds = wishlistItems.stream()
                .map(item -> item.getGame().getCover())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> coverUrlMap = new HashMap<>();
        if (!coverIds.isEmpty()) {
            coverService.getCoversByIds(coverIds)
                    .forEach(dto -> coverUrlMap.put(dto.getId(), dto.getUrl()));
        }

        return wishlistItems.stream()
                .map(item -> {
                    Long coverId = item.getGame().getCover();
                    String coverUrl = coverId != null ? coverUrlMap.get(coverId) : null;
                    return WishlistItemDto.from(item, coverUrl);
                })
                .toList();
    }

    @Transactional
    public WishlistItemDto addToWishlist(AddToWishlistRequest request, UserDetailsImpl principal) {
        if(wishListFull(principal.getUserId()))
            throw new InternalServerException(principal.getUserId() + " - this users wishlist is already full");
        User user = userService.findUserById(principal.getUserId());

        Long gameId = parseGameId(request.getGameId());
        GameEntity game = gameService.getGameById(gameId);

        wishlistRepository.findByUserIdAndGameId(user.getUserId(), gameId)
                .ifPresent(existing -> {
                    throw new DuplicatedResourceException(ApiResponses.WISHLIST_GAME_ALREADY_EXISTS);
                });

        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setGame(game);

        try {
            WishlistItem saved = wishlistRepository.save(item);
            String coverUrl = resolveCoverUrl(game.getCover());
            return WishlistItemDto.from(saved, coverUrl);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedResourceException(ApiResponses.WISHLIST_GAME_ALREADY_EXISTS);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    @Transactional
    public void removeFromWishlist(String wishlistItemId, UserDetailsImpl principal) {
        WishlistItem item = wishlistRepository.findByWishlistItemId(wishlistItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found: " + wishlistItemId));

        if (!item.getUser().getUserId().equals(principal.getUserId())) {
            throw new ForbiddenException();
        }

        wishlistRepository.delete(item);
    }

    private String resolveCoverUrl(Long coverId) {
        if (coverId == null) return null;
        return coverService.getCoversByIds(List.of(coverId)).stream()
                .findFirst()
                .map(dto -> dto.getUrl())
                .orElse(null);
    }

    private boolean wishListFull(String userId){
        List<WishlistItem> items = wishlistRepository.findByUserId(userId);
        return items.size() > MAX_WISHLIST_ITEMS;
    }
    private Long parseGameId(String gameId) {
        try {
            return Long.parseLong(gameId);
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException("Invalid game ID: " + gameId);
        }
    }
}
