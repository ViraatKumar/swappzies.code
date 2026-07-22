package com.swapper.monolith.ChatService.repository;

import com.swapper.monolith.ChatService.entity.Conversation;
import com.swapper.monolith.ChatService.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {

    Optional<ConversationParticipant> findByConversationAndUserUserId(Conversation conversation, String userId);

    List<ConversationParticipant> findAllByConversation(Conversation conversation);
}
