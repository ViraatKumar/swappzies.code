package com.swapper.monolith.ItemService.service;

import com.swapper.monolith.ItemService.mapper.PlatformMapper;
import com.swapper.monolith.ItemService.repository.PlatformRepository;
import com.swapper.monolith.exception.CustomExceptions.InternalServerException;
import com.swapper.monolith.external.dto.PlatformDto;
import com.swapper.monolith.external.twitch.GameApi;
import com.swapper.monolith.model.PlatformEntity;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlatformService {
    Logger LOGGER = LoggerFactory.getLogger(PlatformService.class);

    PlatformRepository platformRepository;
    GameApi gameApi;

    static String URL = "https://api.igdb.com/v4/platforms";
    static String BODY = "fields abbreviation,alternative_name,category,checksum,created_at,generation,name,platform_family,platform_logo,platform_type,slug,summary,updated_at,url,versions,websites; limit 500; offset 0;";
    private final TransactionTemplate transactionTemplate;

    public List<PlatformDto> getPlatforms(){
        List<PlatformEntity> platformEntity = platformRepository.findAll();
        if(!platformEntity.isEmpty()){
            return platformEntity.stream().map(PlatformMapper::toDto).toList();
        }
        return getFromIGDB().stream().map(PlatformMapper::toDto).toList();
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
}
