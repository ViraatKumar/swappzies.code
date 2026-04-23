package com.swapper.monolith.IngestionService.impl;

import com.swapper.monolith.ItemService.service.GenreService;
import com.swapper.monolith.external.dto.GenreDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IngestionServiceImpl {
    Logger LOGGER = LoggerFactory.getLogger(IngestionServiceImpl.class);
//    GameRepository gameRepository;
//    GameApi gameApi;

    GenreService genreService;

//    @Async
//    @Transactional
//    public void populateLocalDB(GameSearchResponse externalResponse) {
//        LOGGER.info("Populate local DB");
//            List<GameDto> apiGameDtos = externalResponse.getGameDtoList();
//            if(apiGameDtos == null || apiGameDtos.isEmpty()) return;
//
//            Map<Long,GameDto> apiGames = new HashMap<>();
//            apiGameDtos.forEach(gameDto -> {
//                if(!apiGames.containsKey(gameDto.getId())){
//                    apiGames.put(gameDto.getId(),gameDto);
//                }
//            });
//            Set<Long> apiGameIds = apiGameDtos.stream().map(GameDto::getId).collect(Collectors.toSet());
//            Set<Long> dbGameEntities = new HashSet<>(gameRepository.findIdsByIdLn(apiGameIds));
//            List<GameEntity> newGames =  new ArrayList<>();
//            apiGames.keySet().forEach(gameId -> {
//                if(!dbGameEntities.contains(gameId)){
//                    newGames.add(gameMapper.toEntity(apiGames.get(gameId)));
//                }
//            });
//            gameRepository.saveAll(newGames);
//    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedGenres() {
        List<GenreDto> gameDtos = genreService.getAllGenres();
        genreService.save(gameDtos);
    }

}
