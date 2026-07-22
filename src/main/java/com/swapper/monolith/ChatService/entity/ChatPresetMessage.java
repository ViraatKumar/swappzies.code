package com.swapper.monolith.ChatService.entity;

import com.swapper.monolith.ChatService.dtos.enums.PresetCategory;
import com.swapper.monolith.model.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "chat_preset_message",
        indexes = {
                @Index(name = "idx_chat_preset_active_category", columnList = "is_active, category, display_order"),
                @Index(name = "idx_chat_preset_category", columnList = "category")
        }
)
public class ChatPresetMessage extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 32)
    private PresetCategory category;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
