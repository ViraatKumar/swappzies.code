package com.swapper.monolith.ItemService.repository;

import com.swapper.monolith.ItemService.entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Native;
import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Long> {
    List<GenreEntity> findAllByIdIn(List<Long> ids);
    List<GenreEntity> findAllByNameIn(List<String> names);

    @Query(value="SELECT DISTINCT(name) from genre",nativeQuery = true)
    List<String> findDistinctGenreNames();
}
