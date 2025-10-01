package com.swapper.monolith.repository;

import com.swapper.monolith.model.Roles;
import com.swapper.monolith.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Roles, Long> {
}
