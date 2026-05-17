package com.swapper.monolith.TradeService.repository;

import com.swapper.monolith.TradeService.dto.constant.TradeStatus;
import com.swapper.monolith.TradeService.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, String> {

    Optional<Trade> findByTradeId(String tradeId);

    List<Trade> findByInitiatorUserIdOrReceiverUserId(String initiatorId, String receiverId);

    boolean existsByOfferedListingListingIdAndRequestedListingListingIdAndStatus(
            String offeredListingId, String requestedListingId, TradeStatus status);
}
