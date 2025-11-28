package com.growthtutoring.backend.matching;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentTutorLinkRepository extends JpaRepository<StudentTutorLink, Long> {

    Optional<StudentTutorLink> findByStudent_IdAndTutor_Id(Long studentUserId, Long tutorUserId);

    List<StudentTutorLink> findByTutor_IdAndStatus(Long tutorUserId, StudentTutorLinkStatus status);

    List<StudentTutorLink> findByStudent_IdAndStatus(Long studentUserId, StudentTutorLinkStatus status);
}