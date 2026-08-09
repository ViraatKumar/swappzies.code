package com.swapper.monolith.config;

import com.swapper.monolith.ItemService.constants.Platform;
import com.swapper.monolith.ItemService.entity.PlatformEntity;
import com.swapper.monolith.ItemService.repository.PlatformRepository;
import com.swapper.monolith.ItemService.repository.UserGamePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class PlatformEnabledSeeder implements CommandLineRunner {

    private final PlatformRepository platformRepository;
    private final UserGamePostRepository userGamePostRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("PlatformEnabledSeeder: starting sync of platform.enabled from user listings");

        List<String> inUseNames;
        try (Stream<Platform> platformsInUse = userGamePostRepository.findDistinctPlatformsInUse()) {
            inUseNames = platformsInUse
                    .filter(Objects::nonNull)
                    .map(Platform::getDisplayName)
                    .toList();
        }

        if (inUseNames.isEmpty()) {
            log.info("PlatformEnabledSeeder: no user listings found; nothing to enable");
            return;
        }
        log.info("PlatformEnabledSeeder: {} distinct platforms in use across listings", inUseNames.size());

        List<PlatformEntity> matched = platformRepository.findAllByNameIn(inUseNames);
        if (matched.isEmpty()) {
            log.warn("PlatformEnabledSeeder: {} platform names from listings had no matching platform rows in DB (names={})",
                    inUseNames.size(), inUseNames);
            return;
        }

        List<PlatformEntity> toUpdate = matched.stream()
                .filter(p -> !p.isEnabled())
                .toList();

        if (toUpdate.isEmpty()) {
            log.info("PlatformEnabledSeeder: all {} matched platforms already enabled; no updates needed", matched.size());
            return;
        }

        toUpdate.forEach(p -> p.setEnabled(true));
        platformRepository.saveAll(toUpdate);
        log.info("PlatformEnabledSeeder: enabled {} platform rows: {}",
                toUpdate.size(),
                toUpdate.stream().map(PlatformEntity::getName).toList());
    }
}
