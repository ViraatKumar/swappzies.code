package com.swapper.monolith.TradeService.dto;

import com.swapper.monolith.TradeService.dto.constant.TradeStatus;
import com.swapper.monolith.TradeService.entity.Trade;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Date;

@Getter
@Builder
public class TradeResponse {

    private String tradeId;
    private String initiatorUsername;
    private String receiverUsername;

    private String offeredListingId;
    private String offeredGameName;
    private String offeredPlatform;
    private String offeredCondition;

    private String requestedListingId;
    private String requestedGameName;
    private String requestedPlatform;
    private String requestedCondition;

    private TradeStatus status;
    private String notes;
    private Date createdDate;
    private Instant completedAt;

    public static TradeResponse from(Trade trade) {
        return TradeResponse.builder()
                .tradeId(trade.getTradeId())
                .initiatorUsername(trade.getInitiator().getUsername())
                .receiverUsername(trade.getReceiver().getUsername())
                .offeredListingId(trade.getOfferedListing().getListingId())
                .offeredGameName(trade.getOfferedListing().getGame().getName())
                .offeredPlatform(trade.getOfferedListing().getPlatform().name())
                .offeredCondition(trade.getOfferedListing().getCondition() != null
                        ? trade.getOfferedListing().getCondition().name() : null)
                .requestedListingId(trade.getRequestedListing().getListingId())
                .requestedGameName(trade.getRequestedListing().getGame().getName())
                .requestedPlatform(trade.getRequestedListing().getPlatform().name())
                .requestedCondition(trade.getRequestedListing().getCondition() != null
                        ? trade.getRequestedListing().getCondition().name() : null)
                .status(trade.getStatus())
                .notes(trade.getNotes())
                .createdDate(trade.getCreatedDate())
                .completedAt(trade.getCompletedAt())
                .build();
    }
}
