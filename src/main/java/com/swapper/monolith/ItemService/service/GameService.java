package com.swapper.monolith.ItemService.service;

import com.swapper.monolith.ItemService.dto.FilterGames.GameResponse;
import com.swapper.monolith.ItemService.dto.*;
import com.swapper.monolith.ItemService.repository.CoverRepository;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public GameService(GameApi gameApi, GameRepository gameRepository, GameMapper gameMapper, IngestionService ingestionService, GenreService genreService, PlatformService platformService, @Lazy CoverService coverService) {
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
            List<GameDto> dtos = gameSearchResponse.getGameDtoList();
            return mapToResponses(dtos);
        }
        logger.warn("Weak response from DB - falling back to IGDB for '{}'", gameName);

        GameSearchResponse twitchResponse = gameApi.searchByGameName(gameName);
        logger.debug("IGDB returned {} games for '{}'", twitchResponse.getGameDtoList().size(), gameName);

        ingestionService.populateDB(
                twitchResponse.getGameDtoList(),
                GameDto::getId,
                gameRepository::findIdsByIdLn,
                gameMapper::toEntity,
                gameRepository::saveAll
        );

        return mapToResponses(twitchResponse.getGameDtoList());
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

            List<GameResponse> igdbMapped = mapToResponses(igdbResponse.getGameDtoList());
            return new PageImpl<>(igdbMapped, pageable, igdbMapped.size());
        }

        List<GameDto> content = dbDtoPage.getContent();
        Map<Long, String> coverUrlMap = buildCoverUrlMap(content);
        Map<Long, String> platformMap = buildPlatformNameMap(content);
        Map<Long, String> genreMap = buildGenreNameMap(content);
        return dbDtoPage.map(dto -> create(dto, coverUrlMap, platformMap, genreMap));
    }

    public GameEntity getGameById(long id) {
        return gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game not found"));
    }
    public GameDto getGameDtoById(long id) {
        return gameMapper.toDto(getGameById(id));
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
        return coverService.getCoversByIds(coverIds).stream()
                .filter(c -> c.getUrl() != null)
                .collect(Collectors.toMap(CoverDto::getId, CoverDto::getUrl));
    }

    private List<GameResponse> mapToResponses(List<GameDto> dtos) {
        Map<Long, String> coverUrlMap = buildCoverUrlMap(dtos);
        Map<Long, String> platformMap = buildPlatformNameMap(dtos);
        Map<Long, String> genreMap = buildGenreNameMap(dtos);
        return dtos.stream().map(dto -> create(dto, coverUrlMap, platformMap, genreMap)).toList();
    }

    private GameResponse create(GameDto gameDto, Map<Long, String> coverUrlMap,
                                 Map<Long, String> platformMap, Map<Long, String> genreMap) {
        GameResponse gameResponse = new GameResponse();
        gameResponse.setId(gameDto.getId());
        gameResponse.setName(gameDto.getName());
        gameResponse.setPlatform(resolveNames(gameDto.getPlatforms(), platformMap));
        gameResponse.setGenre(resolveNames(gameDto.getGenres(), genreMap));
        if (gameDto.getCover() != null) {
            gameResponse.setCoverUrl(coverUrlMap.get(gameDto.getCover()));
        }
        return gameResponse;
    }

    private List<String> resolveNames(List<Long> ids, Map<Long, String> nameMap) {
        if (ids == null || ids.isEmpty()) return null;
        return ids.stream().map(nameMap::get).filter(Objects::nonNull).toList();
    }

    private Map<Long, String> buildPlatformNameMap(List<GameDto> dtos) {
        Set<Long> ids = dtos.stream()
                .filter(d -> d.getPlatforms() != null)
                .flatMap(d -> d.getPlatforms().stream())
                .collect(Collectors.toSet());
        return platformService.getPlatformNameMap(ids);
    }

    private Map<Long, String> buildGenreNameMap(List<GameDto> dtos) {
        Set<Long> ids = dtos.stream()
                .filter(d -> d.getGenres() != null)
                .flatMap(d -> d.getGenres().stream())
                .collect(Collectors.toSet());
        return genreService.getGenreNameMap(ids);
    }

    public List<GameEntity> getGamesByIds(List<Long> gameIds) {
        return gameRepository.findAllById(gameIds);
    }

}
