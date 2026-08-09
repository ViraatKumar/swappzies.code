package com.swapper.monolith.ItemService.service;

import com.swapper.monolith.ItemService.mapper.GenreMapper;
import com.swapper.monolith.ItemService.repository.GenreRepository;
import com.swapper.monolith.external.ExternalApiClient;
import com.swapper.monolith.ItemService.dto.GameDataValues;
import com.swapper.monolith.ItemService.dto.GenreDto;
import com.swapper.monolith.ItemService.entity.GenreEntity;
import com.swapper.monolith.external.twitch.GameApi;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreService {
    Logger logger = LoggerFactory.getLogger(GenreService.class);
    GenreRepository genreRepository;
    private final ExternalApiClient client;
    GameApi gameApi;
    ApplicationContext applicationContext;
    private static final String EXT_API_URL = "https://api.igdb.com/v4/genres";
    private static final String GENRE_QUERY = "fields checksum,created_at,name,slug,updated_at,url; limit 500;";
    public GameDataValues getGenres() {
        List<String> names = genreRepository.findDistinctGenreNames();
        if(!names.isEmpty()) {
            return GameDataValues.builder()
                    .values(names)
                    .build();
        }
        List<GenreDto> genreDtos = getGenresExt();
        applicationContext.getBean(GenreService.class).save(genreDtos);
        return GameDataValues.builder()
                .values(genreDtos.stream().map(GenreDto::getName).collect(Collectors.toList()))
                .build();
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

    @Async
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
    public List<String> getGenresByIds(List<Long> ids) {
        if(ids == null || ids.isEmpty()){
            return null;
        }
        return genreRepository.findAllByIdIn(ids).stream().map(GenreEntity::getName).toList();

    }
    public List<GenreDto> getGenresByName(List<String> genreNames){
        List<GenreEntity> genreEntity = genreRepository.findAllByNameIn(genreNames);
        return genreEntity.stream().map(GenreMapper::toDto).toList();
    }

    public Map<Long, String> getGenreNameMap(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyMap();
        return genreRepository.findAllByIdIn(new ArrayList<>(ids)).stream()
                .collect(Collectors.toMap(GenreEntity::getId, GenreEntity::getName));
    }

}
