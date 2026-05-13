package com.swapper.monolith.WishlistService.repository;

import com.swapper.monolith.WishlistService.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, String> {

    @Query("SELECT w FROM WishlistItem w WHERE w.user.userId = :userId")
    List<WishlistItem> findByUserId(@Param("userId") String userId);

    @Query("SELECT w FROM WishlistItem w WHERE w.user.userId = :userId AND w.game.id = :gameId")
    Optional<WishlistItem> findByUserIdAndGameId(
            @Param("userId") String userId,
            @Param("gameId") Long gameId);

    Optional<WishlistItem> findByWishlistItemId(String wishlistItemId);
}
