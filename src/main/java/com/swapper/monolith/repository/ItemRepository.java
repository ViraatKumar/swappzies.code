package com.swapper.monolith.repository;

import com.swapper.monolith.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
