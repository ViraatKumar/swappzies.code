package com.swapper.monolith.service;

import com.swapper.monolith.TradeService.dto.Game;
import com.swapper.monolith.TradeService.repository.GameRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class GameService {
    GameRepository gameRepository;
    public Game getGameById(String gameId){
        return gameRepository.findByGameId(gameId).orElse(null);
    }
}
