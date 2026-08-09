package com.swapper.monolith.ItemService.service;

import com.swapper.monolith.ItemService.constants.Platform;
import com.swapper.monolith.ItemService.dto.GameDataValues;
import com.swapper.monolith.ItemService.mapper.PlatformMapper;
import com.swapper.monolith.ItemService.repository.PlatformRepository;
import com.swapper.monolith.exception.CustomExceptions.InternalServerException;
import com.swapper.monolith.ItemService.dto.PlatformDto;
import com.swapper.monolith.ItemService.entity.PlatformEntity;
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
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlatformService {
    Logger LOGGER = LoggerFactory.getLogger(PlatformService.class);

    PlatformRepository platformRepository;
    GameApi gameApi;

    static String URL = "https://api.igdb.com/v4/platforms";
    static String BODY = "fields abbreviation,alternative_name,category,checksum,created_at,generation,name,platform_family,platform_logo,platform_type,slug,summary,updated_at,url,versions,websites; limit 500; offset 0;";
    private final TransactionTemplate transactionTemplate;
    ApplicationContext applicationContext;
    public GameDataValues getPlatforms(){
        List<String> names = platformRepository.findDistinctNames();
        if(names.isEmpty()){
            List<PlatformDto> platformDtos  = getFromIGDB().stream().map(PlatformMapper::toDto).toList();
            applicationContext.getBean(PlatformService.class).save(platformDtos);
            return GameDataValues.builder()
                    .values(platformDtos.stream().map(PlatformDto::getName).toList())
                    .build();
        }
        return GameDataValues.builder()
                .values(names)
                .build();
    }

    public List<PlatformDto> getPlatformDtos(List<String> platformNames){
        List<PlatformEntity> platformEntities = platformRepository.findAllByNameIn(platformNames);
        return platformEntities.stream().map(PlatformMapper::toDto).toList();
    }
    @Transactional
    public void save(List<PlatformDto> platformDto){
        if(platformDto.isEmpty()){
            throw new RuntimeException("Platform DTO is empty");
        }
        List<PlatformEntity> platformEntities = platformDto.stream().map(PlatformMapper::toEntity).toList();
        platformRepository.saveAll(platformEntities);
    }
    public List<String> getPlatformFromIds(List<Long> ids){
        if(ids == null || ids.isEmpty()){
            return null;
        }
        return platformRepository.findAllByIdIn(ids).stream().map(PlatformEntity::getName).toList();
    }

    public Map<Long, String> getPlatformNameMap(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyMap();
        return platformRepository.findAllByIdIn(new ArrayList<>(ids)).stream()
                .collect(Collectors.toMap(PlatformEntity::getId, PlatformEntity::getName));
    }
    private List<PlatformEntity> getFromIGDB(){

        try {
            List<PlatformEntity> platformEntity = List.of(gameApi.searchFromIGDB(URL,
                    HttpMethod.POST,
                    BODY,
                    PlatformEntity[].class));
            if(platformEntity.isEmpty()){
                throw new InternalServerException("Data not found from IGDB");
            }
            return platformEntity;
        }
        catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * This needs to be triggered if
     * 1. new platform is added tht we previously didnt have stored so we enable the platform
     *
     * Not handling below right now
     * 2. if there is a deletion of a game in anyway and games of this platform no longer exist we can get rid of it safely
     * @param platform
     * @param isEnabled
     */
    @Async
    @Transactional
    public void togglePlatformEnablement(Platform platform, boolean isEnabled){
        PlatformEntity entity = platformRepository.findByName(platform.getDisplayName());
        if (entity != null) {
            entity.setEnabled(isEnabled);
            platformRepository.save(entity);
        }
    }
}
