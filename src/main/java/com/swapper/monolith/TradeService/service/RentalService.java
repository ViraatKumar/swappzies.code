package com.swapper.monolith.TradeService.service;

import com.swapper.monolith.ItemService.constants.ItemStatus;
import com.swapper.monolith.ItemService.constants.ListingState;
import com.swapper.monolith.ItemService.constants.OfferType;
import com.swapper.monolith.ItemService.entity.UserGamePost;
import com.swapper.monolith.ItemService.service.ItemListingService;
import com.swapper.monolith.TradeService.dto.CreateRentalRequest;
import com.swapper.monolith.TradeService.dto.RentalResponse;
import com.swapper.monolith.TradeService.dto.constant.RentalStatus;
import com.swapper.monolith.TradeService.entity.Rental;
import com.swapper.monolith.TradeService.repository.RentalRepository;
import com.swapper.monolith.exception.CustomExceptions.DuplicatedResourceException;
import com.swapper.monolith.exception.CustomExceptions.ForbiddenException;
import com.swapper.monolith.exception.CustomExceptions.ResourceNotFoundException;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.User;
import com.swapper.monolith.repository.UserRepository;
import com.swapper.monolith.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ItemListingService itemListingService;
    private final UserRepository userRepository;

    @Transactional
    public RentalResponse initiateRental(CreateRentalRequest request, UserDetailsImpl principal) {
        UserGamePost listing = itemListingService.getListingEntityById(request.getListingId());

        if (listing.getListingState() != ListingState.ACTIVE
                || listing.getItemStatus() != ItemStatus.AVAILABLE
                || !listing.getOfferTypes().contains(OfferType.RENT)) {
            throw new ResourceNotFoundException(ApiResponses.RENTAL_LISTING_NOT_RENTABLE.getMessage());
        }

        String ownerId = listing.getUser().getUserId();
        if (ownerId.equals(principal.getUserId())) {
            throw new ResourceNotFoundException("You cannot rent your own listing");
        }

        User renter = userRepository.findByUserId(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User owner = userRepository.findByUserId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        itemListingService.updateListingItemStatus(request.getListingId(), ItemStatus.NOT_AVAILABLE);

        Rental rental = new Rental();
        rental.setRenter(renter);
        rental.setOwner(owner);
        rental.setListing(listing);
        rental.setStatus(RentalStatus.PENDING);
        rental.setRentalStartDate(request.getRentalStartDate());
        rental.setRentalEndDate(request.getRentalEndDate());
        rental.setNotes(request.getNotes());

        return RentalResponse.from(rentalRepository.save(rental));
    }

    @Transactional
    public RentalResponse acceptRental(String rentalId, UserDetailsImpl principal) {
        Rental rental = findRental(rentalId);

        if (!rental.getOwner().getUserId().equals(principal.getUserId())) {
            throw new ForbiddenException();
        }
        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new DuplicatedResourceException(ApiResponses.RENTAL_INVALID_STATUS_TRANSITION);
        }

        rental.setStatus(RentalStatus.ACTIVE);
        return RentalResponse.from(rentalRepository.save(rental));
    }

    public List<RentalResponse> getRentalsForUser(UserDetailsImpl principal) {
        return rentalRepository.findByRenterUserIdOrOwnerUserId(
                        principal.getUserId(), principal.getUserId())
                .stream()
                .map(RentalResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalResponse markReturned(String rentalId, UserDetailsImpl principal) {
        Rental rental = findRental(rentalId);

        if (!rental.getOwner().getUserId().equals(principal.getUserId())) {
            throw new ForbiddenException();
        }
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new DuplicatedResourceException(ApiResponses.RENTAL_INVALID_STATUS_TRANSITION);
        }

        itemListingService.updateListingItemStatus(rental.getListing().getListingId(), ItemStatus.AVAILABLE);

        rental.setStatus(RentalStatus.RETURNED);
        rental.setReturnedAt(Instant.now());
        return RentalResponse.from(rentalRepository.save(rental));
    }

    @Transactional
    public RentalResponse cancelRental(String rentalId, UserDetailsImpl principal) {
        Rental rental = findRental(rentalId);

        boolean isRenter = rental.getRenter().getUserId().equals(principal.getUserId());
        boolean isOwner = rental.getOwner().getUserId().equals(principal.getUserId());

        if (!isRenter && !isOwner) {
            throw new ForbiddenException();
        }
        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new DuplicatedResourceException(ApiResponses.RENTAL_INVALID_STATUS_TRANSITION);
        }

        itemListingService.updateListingItemStatus(rental.getListing().getListingId(), ItemStatus.AVAILABLE);

        rental.setStatus(RentalStatus.CANCELLED);
        return RentalResponse.from(rentalRepository.save(rental));
    }

    private Rental findRental(String rentalId) {
        return rentalRepository.findByRentalId(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException(ApiResponses.RENTAL_NOT_FOUND.getMessage()));
    }
}
