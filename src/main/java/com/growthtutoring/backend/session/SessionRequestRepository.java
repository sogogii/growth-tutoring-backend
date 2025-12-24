package com.growthtutoring.backend.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SessionRequestRepository extends JpaRepository<SessionRequest, Long> {

    /**
     * Find all session requests for a specific tutor
     */
    List<SessionRequest> findByTutorUserIdOrderByRequestedStartAsc(Long tutorUserId);

    /**
     * Find all session requests for a specific student
     */
    List<SessionRequest> findByStudentUserIdOrderByRequestedStartDesc(Long studentUserId);

    /**
     * Find pending requests for a tutor
     */
    List<SessionRequest> findByTutorUserIdAndStatusOrderByRequestedStartAsc(
            Long tutorUserId,
            SessionRequestStatus status
    );

    /**
     * Find upcoming sessions for a tutor (accepted requests after current time)
     */
    @Query("SELECT sr FROM SessionRequest sr WHERE sr.tutorUserId = :tutorUserId " +
            "AND sr.status = 'ACCEPTED' AND sr.requestedStart >= :now " +
            "ORDER BY sr.requestedStart ASC")
    List<SessionRequest> findUpcomingSessionsForTutor(
            @Param("tutorUserId") Long tutorUserId,
            @Param("now") Instant now
    );

    /**
     * Find upcoming sessions for a student
     */
    @Query("SELECT sr FROM SessionRequest sr WHERE sr.studentUserId = :studentUserId " +
            "AND sr.status = 'ACCEPTED' AND sr.requestedStart >= :now " +
            "ORDER BY sr.requestedStart ASC")
    List<SessionRequest> findUpcomingSessionsForStudent(
            @Param("studentUserId") Long studentUserId,
            @Param("now") Instant now
    );

    /**
     * Check if tutor has conflicting session at given time
     */
    @Query("SELECT COUNT(sr) > 0 FROM SessionRequest sr WHERE sr.tutorUserId = :tutorUserId " +
            "AND sr.status = 'ACCEPTED' " +
            "AND ((sr.requestedStart < :end AND sr.requestedEnd > :start))")
    boolean hasConflictingSession(
            @Param("tutorUserId") Long tutorUserId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}