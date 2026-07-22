package com.swapper.monolith.ChatService.repository;

import com.swapper.monolith.ChatService.entity.ChatMessage;
import com.swapper.monolith.ChatService.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByConversationOrderByCreatedAtDesc(Conversation conversation, Pageable pageable);

    long countByConversationAndIdGreaterThanAndSenderUserIdNot(Conversation conversation,
                                                               Long greaterThanMessageId,
                                                               String excludedSenderUserId);

    long countByConversationAndSenderUserIdNot(Conversation conversation, String excludedSenderUserId);
}
