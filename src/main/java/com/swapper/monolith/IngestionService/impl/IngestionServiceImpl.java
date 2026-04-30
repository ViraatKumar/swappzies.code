package com.swapper.monolith.IngestionService.impl;

import com.swapper.monolith.ItemService.service.GenreService;
import com.swapper.monolith.ItemService.service.PlatformService;
import com.swapper.monolith.external.dto.GenreDto;
import com.swapper.monolith.external.dto.PlatformDto;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IngestionServiceImpl {
    PlatformService platformService;
    GenreService genreService;

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
