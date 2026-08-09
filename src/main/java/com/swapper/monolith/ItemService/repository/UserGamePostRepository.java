package com.swapper.monolith.ItemService.repository;

import com.swapper.monolith.ItemService.constants.ListingState;
import com.swapper.monolith.ItemService.constants.Platform;
import com.swapper.monolith.ItemService.entity.GameEntity;
import com.swapper.monolith.ItemService.entity.UserGamePost;
import com.swapper.monolith.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserGamePostRepository extends JpaRepository<UserGamePost, String>,
        JpaSpecificationExecutor<UserGamePost> {


    @Query("SELECT ugp FROM UserGamePost ugp WHERE ugp.user.userId = :userId")
    Page<UserGamePost> findByUserId(@Param("userId") String userId, Pageable pageable);
    List<UserGamePost> findByGame(GameEntity game);
    UserGamePost findByGameAndUser(GameEntity game, User user);
    Optional<UserGamePost> findByListingId(String listingId);

    @Query("SELECT ugp FROM UserGamePost ugp WHERE ugp.user.userId = :userId AND ugp.listingState = :listingState")
    Page<UserGamePost> findByUserIdAndListingState(String userId, ListingState listingState,Pageable pageable);

    @Query("SELECT COUNT(ugp) FROM UserGamePost ugp WHERE ugp.user.userId = :userId AND ugp.listingState = :listingState")
    long countByUserIdAndListingState(@Param("userId") String userId, @Param("ListingState") ListingState listingState);

    @Query("SELECT ugp FROM UserGamePost ugp WHERE ugp.user.userId = :userId AND ugp.game.id = :gameId AND ugp.platform = :platform AND ugp.deletedAt IS NULL")
    Optional<UserGamePost> findActiveByUserGamePlatform(@Param("userId") String userId, @Param("gameId") Long gameId, @Param("platform") Platform platform);


    @Query("SELECT DISTINCT ugp.platform FROM UserGamePost ugp")
    Stream<Platform> findDistinctPlatformsInUse();
}
