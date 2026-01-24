package com.swapper.monolith.ItemService.service;

import com.swapper.monolith.ItemService.dto.GameDto;
import com.swapper.monolith.ItemService.dto.GameSearchResponse;
import com.swapper.monolith.ItemService.entity.GameEntity;
import com.swapper.monolith.ItemService.repository.GameRepository;
import com.swapper.monolith.external.twitch.GameApi;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GameService {

    @Getter
    private GameService self;
    Logger logger = LoggerFactory.getLogger(GameService.class);
    private final GameApi gameApi;
    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;

    public GameService(@Lazy GameService self, GameApi gameApi, GameRepository gameRepository, ModelMapper modelMapper) {
        this.self = self;
        this.gameApi = gameApi;
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
    }
    @Autowired
    public void setSelf(@Lazy GameService self){
        this.self = self;
    }

    /*
    Step 1: Search DB first - if respsonse is weak then search API as well
    Step 2: Combine the responses by unique ID's and return
    Step 3: in an async operation - populate DB with ID's it did not have before
     */
    public GameSearchResponse getGameByName(String gameName){
        if(gameName.length()<=3){
            logger.warn("Game name length should be greater than 3 characters");
            return new GameSearchResponse(List.of(new GameDto()));
        }
        logger.debug("Getting Game by Name {}", gameName);
        List<GameEntity> gameEntities = gameRepository.findGamesOfSimilarName(gameName);
        GameSearchResponse gameSearchResponse = new GameSearchResponse(gameEntities.stream().map(
                game->modelMapper.map(game,GameDto.class)).toList());
        if(isResponseStrong(gameSearchResponse)){
            return gameSearchResponse;
        }
        logger.warn("Weak response from DB - Searching API");

        // if not strong enough then we make call to the IGDB API
        GameSearchResponse twitchResponse = gameApi.searchByGameName(gameName);
        logger.info("Game Search Response from API: {}", twitchResponse.getGameDtoList());

        // runs async to this request
        self.populateLocalDBWithIGDBResponse(twitchResponse,gameSearchResponse);

        return twitchResponse;

    }
    private boolean isResponseStrong(GameSearchResponse gameSearchResponse){
        return gameSearchResponse.getGameDtoList()!=null && gameSearchResponse.getGameDtoList().size()>10;
    }

    @Async
    @Transactional
    protected void populateLocalDBWithIGDBResponse(GameSearchResponse apiSearchResponse,GameSearchResponse dbSearchResponse){

        Map<Long,GameDto> existingGamesInDB = new HashMap<>();

        dbSearchResponse.getGameDtoList().forEach(gameDto -> {
            if(!existingGamesInDB.containsKey(gameDto.getId())){
                existingGamesInDB.put(gameDto.getId(),gameDto);
            }
        });


        apiSearchResponse.getGameDtoList().forEach(gameDto -> {
            if(!existingGamesInDB.containsKey(gameDto.getId())){
                logger.info("Adding Game to DB {} with id {}", gameDto.getName(),gameDto.getId());
                existingGamesInDB.put(gameDto.getId(),gameDto);
                GameEntity gameEntity = modelMapper.map(gameDto,GameEntity.class);
                gameRepository.save(gameEntity);
            }
        });
        logger.info("Successfully added all new games to DB");
    }
}
