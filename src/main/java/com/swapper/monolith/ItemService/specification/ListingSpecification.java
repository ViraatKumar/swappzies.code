package com.swapper.monolith.ItemService.specification;

import com.swapper.monolith.ItemService.constants.Condition;
import com.swapper.monolith.ItemService.constants.OfferType;
import com.swapper.monolith.ItemService.constants.Platform;
import com.swapper.monolith.ItemService.dto.ListingFilterRequest;
import com.swapper.monolith.ItemService.entity.UserGamePost;
import com.swapper.monolith.dto.UserDTO;
import com.swapper.monolith.exception.CustomExceptions.ResourceNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ListingSpecification {

    private ListingSpecification() {}

    public static Specification<UserGamePost> fromFilter(ListingFilterRequest filter, String userId) {
        return (root, query, cb) -> {
            Platform platform = null;
            try{
                if(filter.getPlatform() != null) {
                    platform = Platform.fromDisplayName(filter.getPlatform());
                }


            }
            catch(IllegalArgumentException e){
                throw new ResourceNotFoundException("Invalid platform:" + filter.getPlatform());

            }
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude soft-deleted listings
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (userId != null ){
                predicates.add(cb.notEqual(root.get("user").get("userId"), userId));
            }
            if (filter.getPlatform() != null) {
                try {
                    predicates.add(cb.equal(root.get("platform"), platform));
                } catch (IllegalArgumentException ignored) {}
            }

            if (filter.getGameId() != null) {
                predicates.add(cb.equal(root.get("id").get("gameId"), filter.getGameId()));
            }

            if (filter.getListingType() != null) {
                try {
                    OfferType offerType = OfferType.valueOf(filter.getListingType());
                    Join<UserGamePost, OfferType> offerJoin = root.join("offerTypes", JoinType.INNER);
                    predicates.add(cb.equal(offerJoin, offerType));
                    query.distinct(true);
                } catch (IllegalArgumentException ignored) {}
            }

            if (filter.getCondition() != null) {
                try {
                    Condition condition = Condition.valueOf(filter.getCondition());
                    predicates.add(cb.equal(root.get("condition"), condition));
                } catch (IllegalArgumentException ignored) {}
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            // TODO: PostGIS extension required for geospatial distance filtering
            // if (filter.getLat() != null && filter.getLng() != null && filter.getRadiusKm() != null) { ... }

            // Apply ordering (skip for count queries)
            if (query.getResultType() != Long.class) {
                query.orderBy(
                    cb.desc(root.get("featuredPriority")),
                    cb.desc(root.get("createdDate"))
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
