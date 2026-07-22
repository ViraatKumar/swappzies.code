package com.swapper.monolith.ItemService.service;

import com.swapper.monolith.ItemService.constants.Condition;
import com.swapper.monolith.ItemService.constants.ItemStatus;
import com.swapper.monolith.ItemService.constants.ListingState;
import com.swapper.monolith.ItemService.constants.Platform;
import com.swapper.monolith.ItemService.dto.ListingFilterRequest;
import com.swapper.monolith.ItemService.dto.UserGamePost.CreateListingRequest;
import com.swapper.monolith.ItemService.dto.UserGamePost.UpdateListingRequest;
import com.swapper.monolith.ItemService.dto.UserGamePost.UserGamePostDto;
import com.swapper.monolith.ItemService.entity.GameEntity;
import com.swapper.monolith.ItemService.entity.UserGamePost;
import com.swapper.monolith.ItemService.repository.UserGamePostRepository;
import com.swapper.monolith.ItemService.specification.ListingSpecification;
import com.swapper.monolith.dto.UserDTO;
import com.swapper.monolith.exception.CustomExceptions.DuplicatedResourceException;
import com.swapper.monolith.exception.CustomExceptions.ForbiddenException;
import com.swapper.monolith.exception.CustomExceptions.InternalServerException;
import com.swapper.monolith.exception.CustomExceptions.ResourceNotFoundException;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.User;
import com.swapper.monolith.repository.UserRepository;
import com.swapper.monolith.service.UserDetailsImpl;
import com.swapper.monolith.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemListingService {

    private final UserGamePostRepository userGamePostRepository;
    private final UserRepository userRepository;
    private final GameService gameService;
    private final CoverService coverService;


    @Transactional
    public UserGamePostDto createListing(CreateListingRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new InsufficientAuthenticationException("Cannot create listing without authentication");
        }
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserId(principal.getUserId())
                .orElseThrow(() -> new InternalServerException("User not found"));

        GameEntity game = gameService.getGameById(Long.parseLong(request.getGameId()));
        Platform platform = getPlatform(request.getPlatform());
        UserGamePost duplicatedPost = userGamePostRepository.findActiveByUserGamePlatform(user.getUserId(),game.getId(),platform).orElse(null);
        if (duplicatedPost != null) {
            throw new DuplicatedResourceException(ApiResponses.DUPLICATED_RESOURCE);
        }
        UserGamePost userGamePost = persistListing(request, user, game);
        return toDto(userGamePost);
    }

    public UserGamePost persistListing(CreateListingRequest request, User user, GameEntity game) {
        UserGamePost userGamePost = new UserGamePost();
        userGamePost.setUser(user);
        userGamePost.setGame(game);
        userGamePost.setPlatform(getPlatform(request.getPlatform()));
        userGamePost.setCondition(getCondition(request.getCondition()));
        userGamePost.setItemStatus(ItemStatus.AVAILABLE);
        userGamePost.setListingState(ListingState.ACTIVE);
        userGamePost.setOfferTypes(request.getOfferTypes());
        userGamePost.setPrice(request.getPrice());
        try {
            return userGamePostRepository.save(userGamePost);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedResourceException(ApiResponses.DUPLICATED_RESOURCE, "A similar Post already exists, please do not post the same things multiple times");
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public Page<UserGamePostDto> filterListings(ListingFilterRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<UserGamePost> page = userGamePostRepository.findAll(ListingSpecification.fromFilter(request,userDetails.getUserId()), pageable);
        return toDtoPage(page);
    }

    public UserGamePostDto getListingById(String listingId) {
        UserGamePost listing = userGamePostRepository.findByListingId(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found: " + listingId));
        return toDto(listing);
    }

    @Transactional
    public UserGamePostDto updateListing(String listingId, UpdateListingRequest request, UserDetailsImpl principal) {
        UserGamePost listing = userGamePostRepository.findByListingId(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found: " + listingId));

        if (!listing.getUser().getUserId().equals(principal.getUserId())) {
            throw new ForbiddenException();
        }

        if (request.getCondition() != null) {
            listing.setCondition(getCondition(request.getCondition()));
        }
        if (request.getPrice() != null) {
            listing.setPrice(request.getPrice());
            // TODO: call pricingService.recompute(listing) when pricing service exists
        }
        if (request.getOfferTypes() != null) {
            listing.setOfferTypes(request.getOfferTypes());
        }
        if (request.getDescription() != null) {
            listing.setDescription(request.getDescription());
        }
        if (request.getListingState() != null) {
            listing.setListingState(request.getListingState());
        }

        return toDto(userGamePostRepository.save(listing));
    }

    @Transactional
    public void deleteListing(String listingId, UserDetailsImpl principal) {
        UserGamePost listing = userGamePostRepository.findByListingId(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found: " + listingId));

        boolean isOwner = listing.getUser().getUserId().equals(principal.getUserId());
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException();
        }

        // TODO: check active transactions when Transaction entity exists
        // throw new DuplicatedResourceException(ApiResponses.LISTING_ACTIVE_TRANSACTION) when found

        listing.setDeletedAt(Instant.now());
        userGamePostRepository.save(listing);
    }

    @Transactional
    public UserGamePostDto setFeaturedPriority(String listingId, int priority) {
        UserGamePost listing = userGamePostRepository.findByListingId(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found: " + listingId));
        listing.setFeaturedPriority(priority);
        return toDto(userGamePostRepository.save(listing));
    }
    public Page<UserGamePostDto> getUserActiveGamePosts(UserDetailsImpl userDetailsImpl) {
        Pageable page = Pageable.ofSize(10);
        Page<UserGamePost> result = userGamePostRepository.findByUserIdAndListingState(userDetailsImpl.getUserId(), ListingState.ACTIVE, page);
        return toDtoPage(result);
    }

    private UserGamePostDto toDto(UserGamePost listing) {
        Long coverId = listing.getGame().getCover();
        String coverUrl = null;
        if (coverId != null) {
            coverUrl = coverService.getCoversByIds(List.of(coverId)).stream()
                    .findFirst()
                    .map(dto -> dto.getUrl())
                    .orElse(null);
        }
        return UserGamePostDto.from(listing, coverUrl);
    }

    private Page<UserGamePostDto> toDtoPage(Page<UserGamePost> page) {
        List<UserGamePost> content = page.getContent();
        List<Long> coverIds = content.stream()
                .map(p -> p.getGame().getCover())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, String> coverUrlMap = new HashMap<>();
        if (!coverIds.isEmpty()) {
            coverService.getCoversByIds(coverIds)
                    .forEach(dto -> coverUrlMap.put(dto.getId(), dto.getUrl()));
        }
        List<UserGamePostDto> dtos = content.stream()
                .map(p -> {
                    Long coverId = p.getGame().getCover();
                    return UserGamePostDto.from(p, coverId != null ? coverUrlMap.get(coverId) : null);
                })
                .toList();
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    private Condition getCondition(String condition) {
        try {
            return Condition.valueOf(condition);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid condition: " + condition);
        }
    }

    private Platform getPlatform(String platform) {
        try {
            return Platform.fromDisplayName(platform);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid platform: " + platform);
        }
    }
    public Long getUserActiveTrades(String userId, ListingState listingState) {
        return userGamePostRepository.countByUserIdAndListingState(userId,listingState);
    }

    @Transactional
    public void updateListingItemStatus(String listingId, ItemStatus itemStatus) {
        UserGamePost listing = userGamePostRepository.findByListingId(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found: " + listingId));
        listing.setItemStatus(itemStatus);
        userGamePostRepository.save(listing);
    }

    @Transactional
    public void updateListingState(String listingId, ListingState listingState) {
        UserGamePost listing = userGamePostRepository.findByListingId(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found: " + listingId));
        listing.setListingState(listingState);
        userGamePostRepository.save(listing);
    }

    public UserGamePost getListingEntityById(String listingId) {
        return userGamePostRepository.findByListingId(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found: " + listingId));
    }
}
