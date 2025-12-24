package com.growthtutoring.backend.session;

import java.time.Instant;

/**
 * DTO for session request responses
 */
public record SessionRequestDto(
        Long id,
        Long studentUserId,
        String studentFirstName,
        String studentLastName,
        String studentEmail,
        Long tutorUserId,
        String tutorFirstName,
        String tutorLastName,
        String tutorEmail,
        Instant requestedStart,
        Instant requestedEnd,
        String studentTimezone,
        String tutorTimezone,
        String subject,
        String message,
        SessionRequestStatus status,
        Instant createdAt,
        Instant updatedAt,
        Instant respondedAt,
        String tutorResponseMessage
) {}