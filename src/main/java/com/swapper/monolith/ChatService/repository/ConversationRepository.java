package com.swapper.monolith.ChatService.repository;

import com.swapper.monolith.ChatService.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {

    Optional<Conversation> findByConversationId(String conversationId);

    Optional<Conversation> findByParticipantAUserIdAndParticipantBUserId(String participantAUserId,
                                                                        String participantBUserId);

    @Query("""
            SELECT c FROM Conversation c
            WHERE c.participantA.userId = :userId OR c.participantB.userId = :userId
            ORDER BY COALESCE(c.lastMessageAt, c.createdDate) DESC
            """)
    List<Conversation> findAllForUser(@Param("userId") String userId);
}
