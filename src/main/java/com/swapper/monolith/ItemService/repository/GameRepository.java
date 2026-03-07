package com.swapper.monolith.ItemService.repository;

import com.swapper.monolith.ItemService.entity.GameEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, Long> {
    GameEntity findGameById(long id);
    @Query( value = "SELECT * FROM game WHERE name % :gameName " +
            "ORDER BY similarity(name,:gameName) DESC",
            countQuery = "SELECT count(*) FROM game WHERE name % :gameName",
            nativeQuery = true
    )
    Page<GameEntity> findGamesOfSimilarName(@Param("gameName") String gameName,
                                            Pageable pageable);

    @Query(value = "SELECT g.id FROM game g WHERE g.id IN :ids"
            ,nativeQuery = true)
    List<Long> findIdsByIdLn(@Param("ids") Set<Long> ids);
}
