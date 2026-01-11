package com.swapper.monolith.ItemService.repository;

import com.swapper.monolith.ItemService.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, String> {
}
