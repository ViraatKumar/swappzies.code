package com.swapper.monolith.ItemService.repository;

import com.swapper.monolith.model.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Long> {
    List<GenreEntity> findAllByIdIn(List<Long> ids);
    List<GenreEntity> findAllByNameIn(List<String> names);
}
