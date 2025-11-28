package com.growthtutoring.backend.tutor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TutorReviewRepository extends JpaRepository<TutorReview, Long> {

    List<TutorReview> findByTutorId(Long tutorId);

    Optional<TutorReview> findByTutorIdAndStudentId(Long tutorId, Long studentId);

    @Query("SELECT AVG(r.rating) FROM TutorReview r WHERE r.tutor.id = :tutorId")
    BigDecimal computeAverageRating(Long tutorId);

    @Query("SELECT COUNT(r) FROM TutorReview r WHERE r.tutor.id = :tutorId")
    long countByTutorId(Long tutorId);
}
