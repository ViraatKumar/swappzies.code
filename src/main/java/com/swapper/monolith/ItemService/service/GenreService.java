package com.swapper.monolith.ItemService.service;

import com.swapper.monolith.ItemService.entity.GameEntity;
import com.swapper.monolith.ItemService.mapper.GameMapper;
import com.swapper.monolith.ItemService.mapper.GenreMapper;
import com.swapper.monolith.ItemService.repository.GenreRepository;
import com.swapper.monolith.external.ExternalApiClient;
import com.swapper.monolith.external.dto.GenreDto;
import com.swapper.monolith.external.twitch.GameApi;
import com.swapper.monolith.model.GenreEntity;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreService {
    Logger logger = LoggerFactory.getLogger(GenreService.class);
    GenreRepository genreRepository;
    private final ExternalApiClient client;
    GameApi gameApi;

    private static final String EXT_API_URL = "https://api.igdb.com/v4/genres";
    private static final String GENRE_QUERY = "fields checksum,created_at,name,slug,updated_at,url; limit 500;";
    public List<GenreDto> getGenres() {
        List<GenreEntity> genreEntities = genreRepository.findAll();
        if(genreEntities.isEmpty()) {
            return getGenresExt();
        }
        return genreEntities.stream().map(GenreMapper::toDto).toList();
    }

    private List<GenreDto> getGenresExt(){
        List<GenreEntity> genreEntity = List.of(gameApi.searchFromIGDB(EXT_API_URL,
                HttpMethod.POST,
                GENRE_QUERY,
                GenreEntity[].class));
        if(genreEntity == null){
            throw new RuntimeException("Genre not found");
        }
        return genreEntity.stream().map(GenreMapper::toDto).toList();
    }

    @Transactional
    public void save(List<GenreDto> genreDtos) {
        logger.info("Saving genres to DB");
        if(genreDtos == null || genreDtos.isEmpty()) {
            logger.info("Empty Genres Dto - " + genreDtos);
            return;
        }
        Set<Long> existingIds = genreRepository.findAll().stream()
                .map(GenreEntity::getId)
                .collect(java.util.stream.Collectors.toSet());
        List<GenreEntity> newEntities = genreDtos.stream()
                .map(GenreMapper::toEntity)
                .filter(e -> !existingIds.contains(e.getId()))
                .toList();
        if (newEntities.isEmpty()) {
            logger.info("All genres already exist, skipping save");
            return;
        }
        genreRepository.saveAll(newEntities);
    }
    public GenreDto getGenresListByIdsLn(List<Long> id) {
        List<GenreEntity> genreEntities = genreRepository.findAllByIdIn(id);
        GenreEntity genreEntity = genreEntities.isEmpty() ? null : genreEntities.get(0);
        if(genreEntity == null) {
            throw new RuntimeException("Genre not found");
        }
        return GenreMapper.toDto(genreEntity);
    }
    public List<GenreDto> getGenresByName(List<String> genreNames){
        List<GenreEntity> genreEntity = genreRepository.findAllByNameIn(genreNames);
        return genreEntity.stream().map(GenreMapper::toDto).toList();
    }

    Map<Long,GenreDto> getGenresByGenreIds(List<Long> genreIds){

    }

}
