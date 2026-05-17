package com.swapper.monolith.TradeService.repository;

import com.swapper.monolith.TradeService.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {

    Optional<Rental> findByRentalId(String rentalId);

    List<Rental> findByRenterUserIdOrOwnerUserId(String renterId, String ownerId);
}
