package com.swapper.monolith.ChatService.repository;

import com.swapper.monolith.ChatService.dtos.enums.PresetCategory;
import com.swapper.monolith.ChatService.entity.ChatPresetMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatPresetMessageRepository extends JpaRepository<ChatPresetMessage, Long> {

    List<ChatPresetMessage> findAllByIsActiveTrueOrderByCategoryAscDisplayOrderAsc();

    List<ChatPresetMessage> findAllByIsActiveTrueAndCategoryOrderByDisplayOrderAsc(PresetCategory category);

    List<ChatPresetMessage> findAllByOrderByCategoryAscDisplayOrderAsc();
}
