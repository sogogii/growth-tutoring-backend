package com.growthtutoring.backend.tutor;

import java.math.BigDecimal;

/**
 * DTO for Tutor profile view (My Profile page)
 * Simplified DTO with only tutor-specific fields (no user info)
 * Updated to include weeklySchedule
 */
public record TutorProfileDto(
        Long id,
        Long userId,
        String subjectLabel,
        Integer yearsExperience,
        String teachingMethod,
        String headline,
        String bio,
        BigDecimal hourlyRate,
        String verificationTier,
        WeeklySchedule weeklySchedule  // ADDED
) {}