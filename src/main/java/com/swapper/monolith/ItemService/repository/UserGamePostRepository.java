package com.swapper.monolith.ItemService.repository;

import com.swapper.monolith.ItemService.constants.ListingState;
import com.swapper.monolith.ItemService.entity.GameEntity;
import com.swapper.monolith.ItemService.entity.UserGamePost;
import com.swapper.monolith.ItemService.entity.UserGamePostId;
import com.swapper.monolith.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGamePostRepository extends JpaRepository<UserGamePost, UserGamePostId>,
        JpaSpecificationExecutor<UserGamePost> {
    List<UserGamePost> findByUser(User user);
    List<UserGamePost> findByGame(GameEntity game);
    UserGamePost findByGameAndUser(GameEntity game, User user);
    Optional<UserGamePost> findByListingId(String listingId);

    @Query(value = "SELECT ugp FROM UserGamePost ugp WHERE ugp.id.userId = :userId AND ugp.listingState = :listingState")
    List<UserGamePost> findByUserIdAndListingState(String userId, ListingState listingState);
}
