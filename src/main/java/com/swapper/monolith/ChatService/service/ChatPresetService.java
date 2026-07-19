package com.swapper.monolith.ChatService.service;

import com.swapper.monolith.ChatService.dtos.ChatPresetDto;
import com.swapper.monolith.ChatService.dtos.CreatePresetRequest;
import com.swapper.monolith.ChatService.dtos.UpdatePresetRequest;
import com.swapper.monolith.ChatService.dtos.enums.PresetCategory;
import com.swapper.monolith.ChatService.entity.ChatPresetMessage;
import com.swapper.monolith.ChatService.repository.ChatPresetMessageRepository;
import com.swapper.monolith.exception.CustomExceptions.ResourceNotFoundException;
import com.swapper.monolith.exception.enums.ApiResponses;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatPresetService {

    private final ChatPresetMessageRepository presetRepository;

    public List<ChatPresetDto> listActivePresets(PresetCategory category) {
        List<ChatPresetMessage> presets = (category == null)
                ? presetRepository.findAllByIsActiveTrueOrderByCategoryAscDisplayOrderAsc()
                : presetRepository.findAllByIsActiveTrueAndCategoryOrderByDisplayOrderAsc(category);
        return presets.stream().map(ChatPresetDto::from).collect(Collectors.toList());
    }

    public List<ChatPresetDto> listAllPresets() {
        return presetRepository.findAllByOrderByCategoryAscDisplayOrderAsc().stream()
                .map(ChatPresetDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatPresetDto create(CreatePresetRequest request) {
        ChatPresetMessage preset = new ChatPresetMessage();
        preset.setCategory(request.getCategory());
        preset.setContent(request.getContent());
        preset.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        preset.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        return ChatPresetDto.from(presetRepository.save(preset));
    }

    @Transactional
    public ChatPresetDto update(Long id, UpdatePresetRequest request) {
        ChatPresetMessage preset = presetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ApiResponses.CHAT_PRESET_NOT_FOUND.getMessage()));
        if (request.getCategory() != null) preset.setCategory(request.getCategory());
        if (request.getContent() != null) preset.setContent(request.getContent());
        if (request.getDisplayOrder() != null) preset.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsActive() != null) preset.setIsActive(request.getIsActive());
        return ChatPresetDto.from(presetRepository.save(preset));
    }

    @Transactional
    public void delete(Long id) {
        ChatPresetMessage preset = presetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ApiResponses.CHAT_PRESET_NOT_FOUND.getMessage()));
        presetRepository.delete(preset);
    }
}
