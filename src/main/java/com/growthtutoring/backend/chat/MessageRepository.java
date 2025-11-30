package com.growthtutoring.backend.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // existing
    List<Message> findByConversation_IdOrderByCreatedAtAsc(Long conversationId);

    // latest message in a conversation
    Message findTopByConversation_IdOrderByCreatedAtDesc(Long conversationId);

    // unread messages for this user in a single conversation
    long countByConversation_IdAndSender_IdNotAndReadAtIsNull(Long conversationId, Long viewerUserId);

    // total unread messages for a user (for the header badge)
    @Query("""
        SELECT COUNT(m)
        FROM Message m
        WHERE (m.conversation.student.id = :userId
               OR m.conversation.tutor.id = :userId)
          AND m.sender.id <> :userId
          AND m.readAt IS NULL
        """)
    long countUnreadForUser(@Param("userId") Long userId);

    // mark all messages from the other side as read in this conversation
    @Modifying
    @Transactional
    @Query("""
        UPDATE Message m
        SET m.readAt = CURRENT_TIMESTAMP
        WHERE m.conversation.id = :conversationId
          AND m.sender.id <> :userId
          AND m.readAt IS NULL
        """)
    int markConversationRead(@Param("conversationId") Long conversationId,
                             @Param("userId") Long userId);
}
