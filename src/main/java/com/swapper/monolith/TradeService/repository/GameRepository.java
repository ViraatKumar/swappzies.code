package com.swapper.monolith.TradeService.repository;

import com.swapper.monolith.TradeService.dto.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByGameId(String gameId);
}
