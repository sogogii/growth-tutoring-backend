package com.growthtutoring.backend.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Find by status
    Page<Feedback> findByStatus(FeedbackStatus status, Pageable pageable);

    // Find by read status
    Page<Feedback> findByIsRead(Boolean isRead, Pageable pageable);

    // Find by status and read status
    Page<Feedback> findByStatusAndIsRead(FeedbackStatus status, Boolean isRead, Pageable pageable);

    // Find by date range
    Page<Feedback> findByCreatedAtBetween(Instant startDate, Instant endDate, Pageable pageable);

    // Search by name, email, or comment
    @Query("SELECT f FROM Feedback f WHERE " +
            "LOWER(f.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(f.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(f.comment) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Feedback> searchFeedback(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Count by status
    long countByStatus(FeedbackStatus status);

    // Count unread
    long countByIsRead(Boolean isRead);

    // Get all ordered by created date descending
    Page<Feedback> findAllByOrderByCreatedAtDesc(Pageable pageable);
}