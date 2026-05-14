package com.swapper.monolith.ItemService.service;

import com.swapper.monolith.ItemService.dto.FilterGames.GameResponse;
import com.swapper.monolith.ItemService.dto.*;
import com.swapper.monolith.ItemService.specification.GameSpecification;
import com.swapper.monolith.ItemService.entity.GameEntity;
import com.swapper.monolith.ItemService.mapper.GameMapper;
import com.swapper.monolith.ItemService.repository.GameRepository;
import com.swapper.monolith.ItemService.dto.GenreDto;
import com.swapper.monolith.ItemService.dto.PlatformDto;
import com.swapper.monolith.exception.CustomExceptions.ResourceNotFoundException;
import com.swapper.monolith.external.twitch.GameApi;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GameService {

    private final GenreService genreService;
    private final PlatformService platformService;
    Logger logger = LoggerFactory.getLogger(GameService.class);
    private final GameApi gameApi;
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;
    private final int DB_RESULT_STRENGTH = 10;
    private final IngestionService ingestionService;
    private final CoverService coverService;
    private final Set<String> VALID_SORT_KEYS = Set.of("name");
    private final Set<Sort.Direction> VALID_SORT_DIRECTIONS = Set.of(Sort.Direction.ASC, Sort.Direction.DESC);

    public GameService(GameApi gameApi, GameRepository gameRepository, GameMapper gameMapper, IngestionService ingestionService, GenreService genreService, PlatformService platformService, CoverService coverService) {
        this.gameApi = gameApi;
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
        this.ingestionService = ingestionService;
        this.genreService = genreService;
        this.platformService = platformService;
        this.coverService = coverService;
    }

    /*
    Step 1: Search DB first - if response is weak then search API as well
    Step 2: Combine the responses by unique ID's and return
    Step 3: in an async operation - populate DB with ID's it did not have before
     */
    public List<GameResponse> getGameByName(String gameName){
        logger.debug("Getting Game by Name {}", gameName);
        Pageable pageable = PageRequest.of(0, 10);
        Page<GameEntity> gameEntities = gameRepository.findGamesOfSimilarName(gameName,pageable);
        GameSearchResponse gameSearchResponse = new GameSearchResponse(gameEntities.stream().map(gameMapper::toDto).toList());
        if(isResponseStrong(gameSearchResponse)){
            Map<Long, String> coverUrlMap = buildCoverUrlMap(gameSearchResponse.getGameDtoList());
            return gameSearchResponse.getGameDtoList().stream().map(dto -> create(dto, coverUrlMap)).toList();
        }
        logger.warn("Weak response from DB - Searching API");

        // if not strong enough then we make call to the IGDB API
        GameSearchResponse twitchResponse = gameApi.searchByGameName(gameName);
        logger.info("Game Search Response from API: {}", twitchResponse.getGameDtoList());

        // async call
        ingestionService.populateDB(
                twitchResponse.getGameDtoList(),
                GameDto::getId,
                gameRepository::findIdsByIdLn,
                gameMapper::toEntity,
                gameRepository::saveAll
        );

        Map<Long, String> coverUrlMap = buildCoverUrlMap(twitchResponse.getGameDtoList());
        return twitchResponse.getGameDtoList().stream().map(dto -> create(dto, coverUrlMap)).toList();
    }

    public Page<GameResponse> searchGames(GameFilterRequest filter) {
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new InsufficientAuthenticationException("Cannot View games without Authentication");
        }
        GameProcessedFilters gameProcessedFilters = getProcessedFilters(filter);
        Pageable pageable = PageRequest.of(gameProcessedFilters.getPageNo(), gameProcessedFilters.getPageSize(), gameProcessedFilters.getSort());

        Page<GameDto> dbDtoPage = gameRepository.findAll(GameSpecification.fromFilter(gameProcessedFilters), pageable)
                .map(gameMapper::toDto);

        String name = gameProcessedFilters.getName();
        if (name != null && !name.isBlank() && dbDtoPage.getTotalElements() < DB_RESULT_STRENGTH) {
            logger.warn("Weak DB response for name '{}' in searchGames - falling back to IGDB", name);

            GameSearchResponse igdbResponse = gameApi.searchByGameName(name);

            ingestionService.populateDB(
                    igdbResponse.getGameDtoList(),
                    GameDto::getId,
                    gameRepository::findIdsByIdLn,
                    gameMapper::toEntity,
                    gameRepository::saveAll
            );

            Map<Long, String> coverUrlMap = buildCoverUrlMap(igdbResponse.getGameDtoList());
            List<GameResponse> igdbMapped = igdbResponse.getGameDtoList().stream()
                    .map(dto -> create(dto, coverUrlMap)).toList();
            return new PageImpl<>(igdbMapped, pageable, igdbMapped.size());
        }

        Map<Long, String> coverUrlMap = buildCoverUrlMap(dbDtoPage.getContent());
        return dbDtoPage.map(dto -> create(dto, coverUrlMap));
    }

    public GameEntity getGameById(long id) {
        return gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game not found"));
    }

    private GameProcessedFilters getProcessedFilters(GameFilterRequest gameFilterRequest) {
        GameProcessedFilters gameProcessedFilters = new GameProcessedFilters();
        String sortKey;
        Sort.Direction sortDirection;
        int pageNo = 0;
        int pageSize = 10;
        if(gameFilterRequest.getSortKey() == null || !VALID_SORT_KEYS.contains(gameFilterRequest.getSortKey())) {
         sortKey = "name";
        }
        else{
            sortKey = gameFilterRequest.getSortKey();
        }


        if(gameFilterRequest.getSortDirection() == null || !VALID_SORT_DIRECTIONS.contains(gameFilterRequest.getSortDirection())) {
            sortDirection = Sort.Direction.ASC;
        }
        else if(gameFilterRequest.getSortDirection().equals(Sort.Direction.DESC)){
            sortDirection = Sort.Direction.DESC;
        }
        else{
            sortDirection = Sort.Direction.ASC;
        }
        gameProcessedFilters.setSort(Sort.by(sortDirection,sortKey));

        if(gameFilterRequest.getPage() <= 0){
            gameProcessedFilters.setPageNo(pageNo);
        }
        else{
            gameProcessedFilters.setPageNo(gameFilterRequest.getPage());
        }

        if(gameFilterRequest.getPageSize() <= 0 || gameFilterRequest.getPageSize() >= 200){
            gameProcessedFilters.setPageSize(pageSize);
        }
        else{
            gameProcessedFilters.setPageSize(gameFilterRequest.getPageSize());
        }
        List<GenreDto> genreDtos = genreService.getGenresByName(gameFilterRequest.getGenres());
        List<Long> genreIds = genreDtos.stream().map(GenreDto::getId).toList();

        List<PlatformDto> platformDtos = platformService.getPlatformDtos(gameFilterRequest.getPlatforms());
        List<Long> platformIds = platformDtos.stream().map(PlatformDto::getId).toList();

        gameProcessedFilters.setName(gameFilterRequest.getName());
        gameProcessedFilters.setGenreIds(genreIds);
        gameProcessedFilters.setPlatformIds(platformIds);
        return gameProcessedFilters;
    }

    private boolean isResponseStrong(GameSearchResponse gameSearchResponse){
        return gameSearchResponse.getGameDtoList()!=null && gameSearchResponse.getGameDtoList().size()>=DB_RESULT_STRENGTH;
    }


    private Map<Long, String> buildCoverUrlMap(List<GameDto> dtos) {
        List<Long> coverIds = dtos.stream()
                .map(GameDto::getCover)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (coverIds.isEmpty()) return Collections.emptyMap();
        return coverService.getCoverUrls(coverIds).stream()
                .filter(c -> c.getUrl() != null)
                .collect(Collectors.toMap(CoverDto::getId, CoverDto::getUrl));
    }

    private GameResponse create(GameDto gameDto, Map<Long, String> coverUrlMap) {
        GameResponse gameResponse = new GameResponse();
        gameResponse.setId(gameDto.getId());
        gameResponse.setName(gameDto.getName());
        gameResponse.setPlatform(platformService.getPlatformFromIds(gameDto.getPlatforms()));
        gameResponse.setGenre(genreService.getGenresByIds(gameDto.getGenres()));
        if (gameDto.getCover() != null) {
            gameResponse.setCoverUrl(coverUrlMap.get(gameDto.getCover()));
        }
        return gameResponse;
    }

    public List<GameEntity> getGameEntites(List<Long> gameIds) {
        return gameRepository.findAllById(gameIds);
    }

}
