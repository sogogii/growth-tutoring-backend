package com.growthtutoring.backend.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByStudent_IdAndTutor_Id(Long studentUserId, Long tutorUserId);

    List<Conversation> findByStudent_IdOrTutor_Id(Long asStudentId, Long asTutorId);
}