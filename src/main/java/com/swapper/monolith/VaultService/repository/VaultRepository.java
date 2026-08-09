package com.swapper.monolith.VaultService.repository;

import com.swapper.monolith.VaultService.dto.constant.CompletionStatus;
import com.swapper.monolith.VaultService.entity.UserGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaultRepository extends JpaRepository<UserGame, String> {
    @Query("SELECT ug FROM UserGame ug WHERE ug.user.userId = :userId ORDER BY ug.createdDate DESC")
    List<UserGame> findAllByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(ug) FROM UserGame ug WHERE ug.user.userId = :userId")
    long countByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(ug) FROM UserGame ug WHERE ug.user.userId = :userId AND ug.favorite = true")
    long countFavoritesByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(ug) FROM UserGame ug WHERE ug.user.userId = :userId AND ug.completionStatus = :status")
    long countByUserIdAndStatus(@Param("userId") String userId, @Param("status") CompletionStatus status);
}
