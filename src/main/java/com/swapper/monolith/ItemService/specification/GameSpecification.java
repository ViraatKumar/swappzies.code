package com.swapper.monolith.ItemService.specification;

import com.swapper.monolith.ItemService.dto.GameProcessedFilters;
import com.swapper.monolith.ItemService.entity.GameEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GameSpecification {

    private GameSpecification() {}

    public static Specification<GameEntity> fromFilter(GameProcessedFilters filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"
                ));
            }

            addJsonbAnyOf(predicates, root, cb, "genres", filter.getGenreIds());
            addJsonbAnyOf(predicates, root, cb, "platforms", filter.getPlatformIds());

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addJsonbAnyOf(
            List<Predicate> predicates,
            jakarta.persistence.criteria.Root<GameEntity> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            String field,
            List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        Predicate[] anyOf = ids.stream()
                .map(id -> cb.isTrue(
                        cb.function("jsonb_contains", Boolean.class,
                                root.get(field), cb.literal("[" + id + "]"))
                ))
                .toArray(Predicate[]::new);
        predicates.add(cb.or(anyOf));
    }
}
