package com.growthtutoring.backend.api;

import com.growthtutoring.backend.tutor.Tutor;
import com.growthtutoring.backend.tutor.TutorDto;
import com.growthtutoring.backend.tutor.TutorProfileDto;
import com.growthtutoring.backend.tutor.TutorRepository;
import com.growthtutoring.backend.tutor.UpdateTutorProfileRequest;
import com.growthtutoring.backend.tutor.TeachingMethod;
import com.growthtutoring.backend.user.User;
import com.growthtutoring.backend.user.UserRepository;
import com.growthtutoring.backend.user.UserStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for Tutor endpoints
 * Handles both public tutor listing and private profile management
 */
@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    private final TutorRepository tutorRepository;
    private final UserRepository userRepository;

    public TutorController(TutorRepository tutorRepository,
                           UserRepository userRepository) {
        this.tutorRepository = tutorRepository;
        this.userRepository = userRepository;
    }

    // ================= PUBLIC ENDPOINTS (USED BY TUTORS PAGE) =================

    /**
     * GET /api/tutors
     * Returns list of all ACTIVE tutors for the public tutors listing page
     */
    @GetMapping
    public List<TutorDto> getAllTutors() {
        List<Tutor> tutors = tutorRepository.findAll();

        return tutors.stream()
                .filter(tutor -> {
                    // Fetch linked user
                    User user = userRepository.findById(tutor.getUserId()).orElse(null);

                    // Hide tutors with no user OR with non-active status
                    return user != null && user.getStatus() == UserStatus.ACTIVE;
                })
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * GET /api/tutors/{userId}
     * Returns single ACTIVE tutor by user_id for public tutor profile page
     */
    @GetMapping("/{userId}")
    public ResponseEntity<TutorDto> getTutorByUserId(@PathVariable Long userId) {
        Optional<Tutor> optTutor = tutorRepository.findByUserId(userId);

        if (optTutor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tutor tutor = optTutor.get();
        User user = userRepository.findById(tutor.getUserId()).orElse(null);

        // Hide tutor if not ACTIVE
        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toDto(tutor));
    }

    // ================= PROFILE ENDPOINTS FOR "MY PROFILE" =================
    // These are used by the logged-in tutor themself, so we DO NOT filter by ACTIVE status

    /**
     * GET /api/tutors/user/{userId}
     * Returns detailed tutor profile for the My Profile page
     * Used by tutors to view their own profile data
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<TutorProfileDto> getTutorProfileByUserId(@PathVariable Long userId) {
        return tutorRepository.findByUserId(userId)
                .map(this::toProfileDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/tutors/user/{userId}
     * Updates tutor profile including subjects, hourly rate, teaching method, headline, and bio
     * Used by tutors to edit their own profile
     */
    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateTutorProfileByUserId(
            @PathVariable Long userId,
            @RequestBody UpdateTutorProfileRequest req
    ) {
        return tutorRepository.findByUserId(userId)
                .map(tutor -> {
                    // Update subject label
                    if (req.getSubjectLabel() != null) {
                        tutor.setSubjectLabel(req.getSubjectLabel());
                    }

                    // Update hourly rate
                    if (req.getHourlyRate() != null) {
                        tutor.setHourlyRate(req.getHourlyRate());
                    } else if (tutor.getHourlyRate() == null) {
                        tutor.setHourlyRate(BigDecimal.ZERO);
                    }

                    // Update teaching method
                    if (req.getTeachingMethod() != null) {
                        tutor.setTeachingMethod(
                                TeachingMethod.valueOf(req.getTeachingMethod())
                        );
                    }

                    // Update headline (brief summary for tutors listing)
                    if (req.getHeadline() != null) {
                        tutor.setHeadline(req.getHeadline());
                    }

                    // Update bio (detailed content for profile page)
                    if (req.getBio() != null) {
                        tutor.setBio(req.getBio());
                    }

                    Tutor saved = tutorRepository.save(tutor);
                    return ResponseEntity.ok(toProfileDto(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= HELPER MAPPERS =================

    /**
     * Maps Tutor entity to TutorDto for public listing
     * Includes user information (name, email) and tutor details
     */
    private TutorDto toDto(Tutor tutor) {
        User user = userRepository.findById(tutor.getUserId())
                .orElse(null);

        String firstName = user != null ? user.getFirstName() : null;
        String lastName  = user != null ? user.getLastName()  : null;
        String email     = user != null ? user.getEmail()     : null;

        return new TutorDto(
                tutor.getUserId(),
                firstName,
                lastName,
                email,
                tutor.getRatingAvg(),
                tutor.getRatingCount(),
                tutor.getJoinedAt(),
                tutor.getSubjectLabel(),
                tutor.getYearsExperience(),
                tutor.getEducation(),
                tutor.getTeachingMethod(),
                tutor.getHeadline(),
                tutor.getBio(),
                tutor.getHourlyRate(),
                tutor.getVerificationTier() != null
                        ? tutor.getVerificationTier().name()
                        : "TIER_1"
        );
    }

    /**
     * Maps Tutor entity to TutorProfileDto for My Profile page
     * Simplified DTO with only tutor-specific fields (no user info)
     */
    private TutorProfileDto toProfileDto(Tutor tutor) {
        return new TutorProfileDto(
                tutor.getId(),
                tutor.getUserId(),
                tutor.getSubjectLabel(),
                tutor.getYearsExperience(),
                tutor.getTeachingMethod() != null
                        ? tutor.getTeachingMethod().name()
                        : null,
                tutor.getHeadline(),
                tutor.getBio(),
                tutor.getHourlyRate(),
                tutor.getVerificationTier() != null
                        ? tutor.getVerificationTier().name()
                        : "TIER_1"
        );
    }
}