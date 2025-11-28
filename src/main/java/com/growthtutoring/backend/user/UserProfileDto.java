package com.growthtutoring.backend.user;

import java.time.Instant;
import java.time.LocalDate;

public record UserProfileDto(
        Long id,
        String userUid,
        String firstName,
        String lastName,
        String email,
        LocalDate birthday,
        String profileImageUrl,
        String status,
        String role,
        Instant createdAt
) {}
