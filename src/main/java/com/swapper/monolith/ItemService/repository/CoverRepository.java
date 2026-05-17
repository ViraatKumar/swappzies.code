package com.swapper.monolith.ItemService.repository;

import com.swapper.monolith.ItemService.entity.Cover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoverRepository extends JpaRepository<Cover,Long> {
    Optional<Cover> findById(long id);
    List<Cover> findAllByIdIn(List<Long> ids);

    @Query(value = "SELECT c.id FROM cover c WHERE c.id IN :ids", nativeQuery = true)
    List<Long> findIdsByIdIn(@Param("ids") Set<Long> ids);
}
