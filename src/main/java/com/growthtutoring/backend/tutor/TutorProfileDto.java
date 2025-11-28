package com.growthtutoring.backend.tutor;

import java.math.BigDecimal;

public record TutorProfileDto(
        Long id,
        Long userId,
        String subjectLabel,
        Integer yearsExperience,
        String teachingMethod,
        String headline,
        String bio,
        BigDecimal hourlyRate
) {}
