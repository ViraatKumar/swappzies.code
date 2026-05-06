package com.swapper.monolith.ItemService.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IngestionService {

    @Async
    @Transactional
    public <D, E> void populateDB(
            List<D> dtos,
            Function<D, Long> idExtractor,
            Function<Set<Long>, Collection<Long>> existingIdsFetcher,
            Function<D, E> toEntityMapper,
            Consumer<List<E>> saveAll) {

        if (dtos == null || dtos.isEmpty()) return;

        Map<Long, D> dtoMap = new HashMap<>();
        dtos.forEach(dto -> dtoMap.putIfAbsent(idExtractor.apply(dto), dto));

        Set<Long> existingIds = new HashSet<>(existingIdsFetcher.apply(dtoMap.keySet()));

        List<E> newEntities = dtoMap.keySet().stream()
                .filter(id -> !existingIds.contains(id))
                .map(id -> toEntityMapper.apply(dtoMap.get(id)))
                .toList();

        saveAll.accept(newEntities);
    }
    /*
    KEY
     do not delete - this can be used to seed values when required
     */

//    @EventListener(ApplicationReadyEvent.class)
//    public void seedGenres() {
//        List<GenreDto> gameDtos = genreService.getGenres();
//        List<PlatformDto> platformDtos = platformService.getPlatforms();
//        try {
//            genreService.save(gameDtos);
//            platformService.save(platformDtos);
//        }
//        catch(Exception e){
//            throw new RuntimeException(e.getMessage());
//        }
//    }

}
