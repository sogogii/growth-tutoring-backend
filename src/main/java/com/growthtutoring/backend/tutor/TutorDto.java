package com.growthtutoring.backend.tutor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for Tutor public listing
 * Includes user information (name, email) and tutor details
 * Updated to include weeklySchedule
 */
public record TutorDto(
        Long userId,
        String firstName,
        String lastName,
        String email,
        String profileImageUrl,
        BigDecimal ratingAvg,
        Integer ratingCount,
        LocalDate joinedAt,
        String subjectLabel,
        Integer yearsExperience,
        String education,
        TeachingMethod teachingMethod,
        String headline,
        String bio,
        BigDecimal hourlyRate,
        String verificationTier,
        WeeklySchedule weeklySchedule  // ADDED
) {}