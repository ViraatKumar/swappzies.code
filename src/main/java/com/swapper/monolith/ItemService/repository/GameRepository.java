package com.swapper.monolith.ItemService.repository;

import com.swapper.monolith.ItemService.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, String> {
    GameEntity findGameById(long id);
    @Query( value = "SELECT * FROM game WHERE name % :gameName " +
            "ORDER BY similarity(name,:gameName) DESC",
            nativeQuery = true
    )
    List<GameEntity> findGamesOfSimilarName(@Param("gameName") String gameName);
}
