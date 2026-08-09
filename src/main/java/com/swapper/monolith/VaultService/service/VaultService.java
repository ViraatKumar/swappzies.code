package com.swapper.monolith.VaultService.service;

import com.swapper.monolith.ItemService.constants.Platform;
import com.swapper.monolith.ItemService.dto.CoverDto;
import com.swapper.monolith.ItemService.entity.GameEntity;
import com.swapper.monolith.ItemService.service.CoverService;
import com.swapper.monolith.ItemService.service.GameService;
import com.swapper.monolith.VaultService.dto.*;
import com.swapper.monolith.VaultService.dto.constant.CompletionStatus;
import com.swapper.monolith.VaultService.dto.constant.GamerLevelName;
import com.swapper.monolith.VaultService.dto.constant.GamerTitle;
import com.swapper.monolith.VaultService.entity.UserGame;
import com.swapper.monolith.VaultService.repository.VaultRepository;
import com.swapper.monolith.exception.CustomExceptions.ForbiddenException;
import com.swapper.monolith.exception.CustomExceptions.ResourceNotFoundException;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.User;
import com.swapper.monolith.service.UserDetailsImpl;
import com.swapper.monolith.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VaultService {

    private final VaultRepository vaultRepository;
    private final UserService userService;
    private final GameService gameService;
    private final CoverService coverService;

    public VaultEntriesResponse getUserVaultEntries(UserDetailsImpl principal) {
        List<VaultEntryResponse> vaultEntryResponse = vaultRepository.findAllByUserId(
                principal.getUserId())
                .stream()
                .map(VaultEntryResponse::from)
                .toList();
        GamerTitle gamerTitle = GamerTitle.fromGameCount(vaultEntryResponse.size());
        GamerLevelName gamerLevelName = GamerLevelName.fromGameCount(vaultEntryResponse.size());
        return VaultEntriesResponse.createVaultEntriesResponse(vaultEntryResponse,
                gamerTitle.getDisplayName(),
                gamerLevelName.getDisplayName());
    }

    @Transactional
    public VaultEntryResponse createEntry(CreateVaultEntryRequest request, UserDetailsImpl principal) {
        User user = userService.findUserById(principal.getUserId());
        GameEntity game = gameService.getGameById(request.getGameId());
        CoverDto cover = coverService.getCoverById(game.getCover());
        Platform userGamePlatform = Platform.fromDisplayName(request.getPlatform());
        UserGame userGame = new UserGame();
        userGame.setUser(user);
        userGame.setGame(game);
        userGame.setPlatform(userGamePlatform);
        userGame.setCoverUrl(cover.getUrl());
        userGame.setCompletionStatus(request.getCompletion() != null ? request.getCompletion() : CompletionStatus.NOT_STARTED);
        userGame.setFavorite(Boolean.TRUE.equals(request.getFavorite()));

        return VaultEntryResponse.from(vaultRepository.save(userGame));
    }

    @Transactional
    public VaultEntryResponse updateEntry(String userGameId, UpdateVaultEntryRequest request, UserDetailsImpl principal) {
        UserGame userGame = getOwnedEntry(userGameId, principal);

        if (request.getPlatform() != null) userGame.setPlatform(request.getPlatform());
        if (request.getCompletion() != null) userGame.setCompletionStatus(request.getCompletion());
        if (request.getFavorite() != null) userGame.setFavorite(request.getFavorite());

        return VaultEntryResponse.from(vaultRepository.save(userGame));
    }

    @Transactional
    public void deleteEntry(String userGameId, UserDetailsImpl principal) {
        UserGame userGame = getOwnedEntry(userGameId, principal);
        vaultRepository.delete(userGame);
    }

    @Transactional
    public VaultEntryResponse toggleFavorite(String userGameId, UserDetailsImpl principal) {
        UserGame userGame = getOwnedEntry(userGameId, principal);
        userGame.setFavorite(!Boolean.TRUE.equals(userGame.getFavorite()));
        return VaultEntryResponse.from(vaultRepository.save(userGame));
    }

    public VaultStatsResponse getStats(UserDetailsImpl principal) {
        String userId = principal.getUserId();
        return VaultStatsResponse.builder()
                .total(vaultRepository.countByUserId(userId))
                .favorites(vaultRepository.countFavoritesByUserId(userId))
                .notStarted(vaultRepository.countByUserIdAndStatus(userId, CompletionStatus.NOT_STARTED))
                .inProgress(vaultRepository.countByUserIdAndStatus(userId, CompletionStatus.IN_PROGRESS))
                .completed(vaultRepository.countByUserIdAndStatus(userId, CompletionStatus.COMPLETED))
                .abandoned(vaultRepository.countByUserIdAndStatus(userId, CompletionStatus.ABANDONED))
                .build();
    }

    private UserGame getOwnedEntry(String userGameId, UserDetailsImpl principal) {
        UserGame userGame = vaultRepository.findById(userGameId)
                .orElseThrow(() -> new ResourceNotFoundException(ApiResponses.VAULT_ENTRY_NOT_FOUND.getMessage()));
        if (!isGameOwner(userGame,principal.getUserId())) {
            throw new ForbiddenException();
        }
        return userGame;
    }

    private boolean isGameOwner(UserGame userGame, String userId){
        return userGame != null && userGame.getUser().getUserId().equals(userId);
    }
}
