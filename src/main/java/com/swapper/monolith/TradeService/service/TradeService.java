package com.swapper.monolith.TradeService.service;

import com.swapper.monolith.ItemService.constants.ItemStatus;
import com.swapper.monolith.ItemService.constants.ListingState;
import com.swapper.monolith.ItemService.constants.OfferType;
import com.swapper.monolith.ItemService.entity.UserGamePost;
import com.swapper.monolith.ItemService.service.ItemListingService;
import com.swapper.monolith.TradeService.dto.CreateTradeRequest;
import com.swapper.monolith.TradeService.dto.TradeResponse;
import com.swapper.monolith.TradeService.dto.constant.TradeStatus;
import com.swapper.monolith.TradeService.entity.Trade;
import com.swapper.monolith.TradeService.repository.TradeRepository;
import com.swapper.monolith.dto.UserDTO;
import com.swapper.monolith.event.TradeAcceptedEvent;
import com.swapper.monolith.event.TradeCreatedEvent;
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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final ItemListingService itemListingService;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TradeResponse initiateTrade(CreateTradeRequest request, UserDetailsImpl principal) {
        UserGamePost requestedListing = itemListingService.getListingEntityById(request.getRequestedListingId());
        UserGamePost offeredListing = itemListingService.getListingEntityById(request.getOfferedListingId());

        if (requestedListing.getListingState() != ListingState.ACTIVE
                || requestedListing.getItemStatus() != ItemStatus.AVAILABLE
                || !requestedListing.getOfferTypes().contains(OfferType.TRADE)) {
            throw new ResourceNotFoundException(ApiResponses.TRADE_LISTING_NOT_TRADEABLE.getMessage());
        }

        if (offeredListing.getListingState() != ListingState.ACTIVE
                || offeredListing.getItemStatus() != ItemStatus.AVAILABLE
                || !offeredListing.getOfferTypes().contains(OfferType.TRADE)) {
            throw new ResourceNotFoundException(ApiResponses.TRADE_LISTING_NOT_TRADEABLE.getMessage());
        }

        if (!offeredListing.getUser().getUserId().equals(principal.getUserId())) {
            throw new ForbiddenException("The offered listing does not belong to you");
        }

        String receiverId = requestedListing.getUser().getUserId();
        if (receiverId.equals(principal.getUserId())) {
            throw new ResourceNotFoundException(ApiResponses.TRADE_CANNOT_TRADE_OWN_LISTING.getMessage());
        }

        if (tradeRepository.existsByOfferedListingListingIdAndRequestedListingListingIdAndStatus(
                request.getOfferedListingId(), request.getRequestedListingId(), TradeStatus.PENDING)) {
            throw new DuplicatedResourceException(ApiResponses.TRADE_DUPLICATE);
        }

        User initiator = userRepository.findByUserId(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User receiver = userRepository.findByUserId(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver user not found"));

        Trade trade = new Trade();
        trade.setInitiator(initiator);
        trade.setReceiver(receiver);
        trade.setOfferedListing(offeredListing);
        trade.setRequestedListing(requestedListing);
        trade.setStatus(TradeStatus.PENDING);
        trade.setNotes(request.getNotes());

        TradeCreatedEvent tradeCreatedEvent = new TradeCreatedEvent(UserDTO.from(initiator),UserDTO.from(receiver),request);
        eventPublisher.publishEvent(tradeCreatedEvent);
        return TradeResponse.from(tradeRepository.save(trade));
    }

    public List<TradeResponse> getTradesForUser(UserDetailsImpl principal) {
        return tradeRepository.findByInitiatorUserIdOrReceiverUserId(
                        principal.getUserId(), principal.getUserId())
                .stream()
                .map(TradeResponse::from)
                .collect(Collectors.toList());
    }

    public TradeResponse getTradeById(String tradeId, UserDetailsImpl principal) {
        Trade trade = findTrade(tradeId);
        assertParticipant(trade, principal.getUserId());
        return TradeResponse.from(trade);
    }

    @Transactional
    public TradeResponse acceptTrade(String tradeId, UserDetailsImpl principal) {
        Trade trade = findTrade(tradeId);

        if (!trade.getReceiver().getUserId().equals(principal.getUserId())) {
            throw new ForbiddenException();
        }
        if (trade.getStatus() != TradeStatus.PENDING) {
            throw new DuplicatedResourceException(ApiResponses.TRADE_INVALID_STATUS_TRANSITION);
        }

        UserGamePost offered = itemListingService.getListingEntityById(trade.getOfferedListing().getListingId());
        UserGamePost requested = itemListingService.getListingEntityById(trade.getRequestedListing().getListingId());

        if (offered.getListingState() != ListingState.ACTIVE || offered.getItemStatus() != ItemStatus.AVAILABLE
                || requested.getListingState() != ListingState.ACTIVE || requested.getItemStatus() != ItemStatus.AVAILABLE) {
            throw new ResourceNotFoundException(ApiResponses.TRADE_LISTING_NOT_TRADEABLE.getMessage());
        }

        itemListingService.updateListingItemStatus(trade.getOfferedListing().getListingId(), ItemStatus.NOT_AVAILABLE);
        itemListingService.updateListingItemStatus(trade.getRequestedListing().getListingId(), ItemStatus.NOT_AVAILABLE);

        trade.setStatus(TradeStatus.ACCEPTED);
        Trade saved = tradeRepository.save(trade);

        eventPublisher.publishEvent(new TradeAcceptedEvent(
                saved.getTradeId(),
                saved.getInitiator().getUserId(),
                saved.getReceiver().getUserId()
        ));

        return TradeResponse.from(saved);
    }

    @Transactional
    public TradeResponse declineTrade(String tradeId, UserDetailsImpl principal) {
        Trade trade = findTrade(tradeId);

        if (!trade.getReceiver().getUserId().equals(principal.getUserId())) {
            throw new ForbiddenException();
        }
        if (trade.getStatus() != TradeStatus.PENDING) {
            throw new DuplicatedResourceException(ApiResponses.TRADE_INVALID_STATUS_TRANSITION);
        }

        trade.setStatus(TradeStatus.DECLINED);
        return TradeResponse.from(tradeRepository.save(trade));
    }

    @Transactional
    public TradeResponse completeTrade(String tradeId, UserDetailsImpl principal) {
        Trade trade = findTrade(tradeId);
        assertParticipant(trade, principal.getUserId());

        if (trade.getStatus() != TradeStatus.ACCEPTED) {
            throw new DuplicatedResourceException(ApiResponses.TRADE_INVALID_STATUS_TRANSITION);
        }

        itemListingService.updateListingState(trade.getOfferedListing().getListingId(), ListingState.INACTIVE);
        itemListingService.updateListingState(trade.getRequestedListing().getListingId(), ListingState.INACTIVE);

        trade.setStatus(TradeStatus.COMPLETED);
        trade.setCompletedAt(Instant.now());
        return TradeResponse.from(tradeRepository.save(trade));
    }

    @Transactional
    public TradeResponse cancelTrade(String tradeId, UserDetailsImpl principal) {
        Trade trade = findTrade(tradeId);

        if (!trade.getInitiator().getUserId().equals(principal.getUserId())) {
            throw new ForbiddenException();
        }
        if (trade.getStatus() != TradeStatus.PENDING) {
            throw new DuplicatedResourceException(ApiResponses.TRADE_INVALID_STATUS_TRANSITION);
        }

        trade.setStatus(TradeStatus.CANCELLED);
        return TradeResponse.from(tradeRepository.save(trade));
    }

//    @EventListener(ApplicationReadyEvent.class)
    private Trade findTrade(String tradeId) {
        return tradeRepository.findByTradeId(tradeId)
                .orElseThrow(() -> new ResourceNotFoundException(ApiResponses.TRADE_NOT_FOUND.getMessage()));
    }

    private void assertParticipant(Trade trade, String userId) {
        boolean isParticipant = trade.getInitiator().getUserId().equals(userId)
                || trade.getReceiver().getUserId().equals(userId);
        if (!isParticipant) {
            throw new ForbiddenException();
        }
    }
}
