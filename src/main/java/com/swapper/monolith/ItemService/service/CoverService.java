package com.swapper.monolith.ItemService.service;

import com.swapper.monolith.ItemService.dto.CoverDto;
import com.swapper.monolith.ItemService.dto.CoverRequest;
import com.swapper.monolith.ItemService.entity.Cover;
import com.swapper.monolith.ItemService.mapper.CoverMapper;
import com.swapper.monolith.ItemService.repository.CoverRepository;
import com.swapper.monolith.ItemService.repository.GameRepository;
import com.swapper.monolith.exception.CustomExceptions.ResourceNotFoundException;
import com.swapper.monolith.external.twitch.GameApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;

@Slf4j
@Service
public class CoverService {

    private final CoverRepository coverRepository;
    private final CoverMapper coverMapper;
    private final GameApi gameApi;
    private final IngestionService ingestionService;
    private final GameRepository gameRepository;

    public CoverService(CoverRepository coverRepository, CoverMapper coverMapper, GameApi gameApi, IngestionService ingestionService, GameRepository gameRepository) {
        this.coverRepository = coverRepository;
        this.coverMapper = coverMapper;
        this.gameApi = gameApi;
        this.ingestionService = ingestionService;
        this.gameRepository = gameRepository;
    }

    public List<CoverDto> getCoverUrls(CoverRequest coverRequest) {
        List<Long> coverIds = gameRepository.findCoverIdsByIds(coverRequest.getGameIds());
        if (coverIds.isEmpty()) {
            throw new ResourceNotFoundException("no games were found from the game ids - " + coverRequest.getGameIds());
        }
        return fetchCoversByIds(coverIds);
    }

    public String getCoverUrlFromDb(Long coverId) {
        if (coverId == null) return null;
        return coverRepository.findById(coverId).map(Cover::getUrl).orElse(null);
    }

    public List<CoverDto> getCoversByIds(List<Long> coverIds) {
        return fetchCoversByIds(coverIds);
    }
    public CoverDto getCoverById(Long coverId) {
        return fetchCoversByIds(Collections.singletonList(coverId)).getFirst();
    }

    private List<CoverDto> fetchCoversByIds(List<Long> coverIds) {
        List<Cover> dbCovers = coverRepository.findAllByIdIn(coverIds);
        Set<Long> foundIds = dbCovers.stream().map(Cover::getId).collect(Collectors.toSet());

        List<Long> missingIds = coverIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        List<CoverDto> dbResults = dbCovers.stream().map(CoverMapper::toDto).toList();

        if (missingIds.isEmpty()) {
            return dbResults;
        }

        log.warn("Searching IGDB for covers: {}", missingIds);
        List<CoverDto> apiResults = gameApi.searchCoversByIds(missingIds);

        ingestionService.populateDB(
                apiResults,
                CoverDto::getId,
                coverRepository::findIdsByIdIn,
                coverMapper::toEntity,
                coverRepository::saveAll
        );

        List<CoverDto> combined = new ArrayList<>(dbResults);
        combined.addAll(apiResults);
        return combined;
    }

    @Async
    public void populateCoversAsync(List<Long> coverIds) {
        if (coverIds == null || coverIds.isEmpty()) return;

        Set<Long> existingIds = new HashSet<>(coverRepository.findIdsByIdIn(new HashSet<>(coverIds)));
        List<Long> missingIds = coverIds.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        if (missingIds.isEmpty()) return;

        log.info("Populating covers from IGDB for IDs: {}", missingIds);
        List<CoverDto> apiCovers = gameApi.searchCoversByIds(missingIds);

        ingestionService.populateDB(
                apiCovers,
                CoverDto::getId,
                coverRepository::findIdsByIdIn,
                coverMapper::toEntity,
                coverRepository::saveAll
        );
    }
}
