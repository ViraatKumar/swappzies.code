package com.swapper.monolith.ItemService.service;

import com.swapper.monolith.ItemService.dto.GameDTO;
import com.swapper.monolith.ItemService.dto.IgdbGameDto;
import com.swapper.monolith.ItemService.dto.TwitchSearchResponse;
import com.swapper.monolith.ItemService.entity.GameEntity;
import com.swapper.monolith.ItemService.mapper.GameMapper;
import com.swapper.monolith.ItemService.repository.GameRepository;
import com.swapper.monolith.exception.ResourceNotFoundException;
import com.swapper.monolith.external.twitch.GameApi;
import com.swapper.monolith.model.User;
import com.swapper.monolith.service.TradeUserDetailsImpl;
import com.swapper.monolith.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;
    private final UserService userService;
    private final GameApi gameApi;
    @Transactional(readOnly = true)
    public GameDTO getGameById(String gameId) {
        return gameRepository.findById(gameId)
                .map(gameMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));
    }

    @Transactional(readOnly = true)
    public List<GameDTO> getAllGames() {
        return gameRepository.findAll().stream()
                .map(gameMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GameDTO createGame(GameDTO gameDTO) {
        log.info("Creating new game {}", gameDTO.toString());
        GameEntity gameEntity = gameMapper.toEntity(gameDTO);
        if(gameEntity == null)
            throw new IllegalArgumentException("Invalid game entity");


        TradeUserDetailsImpl userDetails = (TradeUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userDetails.getUserId() == null){
            throw new ResourceNotFoundException("user Details do not have user Id - " + userDetails.getUserId());
        }
        User user = userService.findByUserId(userDetails.getUserId());
        if(user == null){
            throw new ResourceNotFoundException("User not found with id: " + userDetails.getUserId());
        }
        gameEntity.setOwner(user);

        GameEntity savedGame = gameRepository.save(gameEntity);

        return gameMapper.toDTO(savedGame);
    }
    @Transactional
    public GameDTO updateGame(String gameId, GameDTO gameDTO) {
        GameEntity existingGame = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        // TODO: Add authorization logic here to ensure the current user owns the game.

        existingGame.setName(gameDTO.getName());
        existingGame.setCondition(gameDTO.getCondition());
        existingGame.setConsole(gameDTO.getConsole());
        existingGame.setRegion(gameDTO.getRegion());
        existingGame.setPrice(gameDTO.getPrice());
        existingGame.setStatus(gameDTO.getStatus());
        existingGame.setUpdatedAt(new Date());

        GameEntity updatedGame = gameRepository.save(existingGame);
        return gameMapper.toDTO(updatedGame);
    }

    @Transactional
    public void deleteGame(String gameId) {
        if (!gameRepository.existsById(gameId)) {
            throw new ResourceNotFoundException("Game not found with id: " + gameId);
        }
        // TODO: Add authorization logic here.
        gameRepository.deleteById(gameId);
    }

    public TwitchSearchResponse getGameByName(String gameName){
        return gameApi.searchByGameName(gameName);
    }
}
