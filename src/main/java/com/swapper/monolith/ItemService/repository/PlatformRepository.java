package com.swapper.monolith.ItemService.repository;

import com.swapper.monolith.ItemService.constants.Platform;
import com.swapper.monolith.ItemService.entity.PlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PlatformRepository extends JpaRepository<PlatformEntity, Long> {
    List<PlatformEntity> findAllByNameIn(List<String> names);
    @Query(value = "SELECT DISTINCT(name) FROM platform",nativeQuery = true)
    List<String> findDistinctNames();

    List<PlatformEntity> findAllByIdIn(Collection<Long> ids);
}
