package com.swapper.monolith.config;

import com.swapper.monolith.dto.enums.Role;
import com.swapper.monolith.model.Roles;
import com.swapper.monolith.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        Set<Role> existing = roleRepository.findAll().stream()
                .map(Roles::getRole)
                .collect(Collectors.toSet());

        List<Roles> rows = new ArrayList<>();
        for (Role role : EnumSet.allOf(Role.class)) {
            if (existing.contains(role)) continue;
            Roles row = new Roles();
            row.setId((long) (role.ordinal() + 1));
            row.setRole(role);
            rows.add(row);
            log.info("Seeded missing role: {}", role);
        }
        roleRepository.saveAll(rows);
    }
}
