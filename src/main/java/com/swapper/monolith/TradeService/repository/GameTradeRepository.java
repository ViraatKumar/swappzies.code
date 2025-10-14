package com.swapper.monolith.TradeService.repository;

import com.swapper.monolith.TradeService.dto.Game;
import com.swapper.monolith.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameTradeRepository extends JpaRepository<Game, Long> {
    Page<Game> findByOwner(User owner, Pageable pageable);
}
