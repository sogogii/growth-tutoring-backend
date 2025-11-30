package com.growthtutoring.backend.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversation_IdOrderByCreatedAtAsc(Long conversationId);

    // Count unread messages for a user:
    // - user participates in conversation (student or tutor)
    // - user is NOT the sender
    // - read == false
    @Query("""
        SELECT COUNT(m)
        FROM Message m
        WHERE (m.conversation.student.id = :userId
               OR m.conversation.tutor.id = :userId)
          AND m.sender.id <> :userId
          AND m.readAt IS NULL
        """)
    long countUnreadForUser(@Param("userId") Long userId);

    // Mark all messages in a conversation as read for a user
    @Modifying
    @Transactional
    @Query("""
        UPDATE Message m
        SET m.readAt = CURRENT_TIMESTAMP
        WHERE m.conversation.id = :conversationId
          AND m.sender.id <> :userId
          AND m.readAt IS NULL
        """)
    int markConversationRead(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId
    );
}