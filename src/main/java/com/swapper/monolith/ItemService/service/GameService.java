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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GameService {

    @Getter
    private GameService self;
    Logger logger = LoggerFactory.getLogger(GameService.class);
    private final GameApi gameApi;
    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;
    private final int DB_RESULT_STRENGTH =10;
    public GameService(GameApi gameApi, GameRepository gameRepository, ModelMapper modelMapper) {
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
        Pageable pageable = PageRequest.of(0, 10);
        Page<GameEntity> gameEntities = gameRepository.findGamesOfSimilarName(gameName,pageable);
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
        self.populateDBWithMissingValues(twitchResponse);

        return twitchResponse;

    }
    private boolean isResponseStrong(GameSearchResponse gameSearchResponse){
        return gameSearchResponse.getGameDtoList()!=null && gameSearchResponse.getGameDtoList().size()>DB_RESULT_STRENGTH;
    }

    /**
     * Search the DB with the id's that we recieved from the IGDB API
     * Push
     * @param apiSearchResponse contains API Search Response from the IGDB API and populates using these games into the service DB if these games are missing
     */
    @Async
    @Transactional
    public void populateDBWithMissingValues(GameSearchResponse apiSearchResponse){
        List<GameDto> apiGameDtos = apiSearchResponse.getGameDtoList();
        if(apiGameDtos == null || apiGameDtos.isEmpty()) return;

        Map<Long,GameDto> apiGames = new HashMap<>();
        apiGameDtos.forEach(gameDto -> {
            if(!apiGames.containsKey(gameDto.getId())){
                apiGames.put(gameDto.getId(),gameDto);
            }
        });
        Set<Long> apiGameIds = apiGameDtos.stream().map(GameDto::getId).collect(Collectors.toSet());
        Set<Long> dbGameEntities = new HashSet<>(gameRepository.findIdsByIdLn(apiGameIds));
        List<GameEntity> newGames =  new ArrayList<>();
        apiGames.keySet().forEach(gameId -> {
            if(!dbGameEntities.contains(gameId)){
                newGames.add(modelMapper.map(apiGames.get(gameId),GameEntity.class));
            }
        });
        gameRepository.saveAll(newGames);
    }
}
