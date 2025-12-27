package com.growthtutoring.backend.session;

import com.growthtutoring.backend.user.User;
import com.growthtutoring.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Instant;

@RestController
@RequestMapping("/api/session-requests")
public class SessionRequestController {

    private final SessionRequestRepository sessionRequestRepository;
    private final UserRepository userRepository;

    // Maximum 3 months in future
    private static final long MAX_DAYS_IN_FUTURE = 90;

    // Minimum 1 hour duration
    private static final long MIN_DURATION_MINUTES = 60;

    public SessionRequestController(
            SessionRequestRepository sessionRequestRepository,
            UserRepository userRepository) {
        this.sessionRequestRepository = sessionRequestRepository;
        this.userRepository = userRepository;
    }

    /**
     * POST /api/session-requests
     * Create a new session request (student only)
     */
    @PostMapping
    public ResponseEntity<?> createSessionRequest(
            @RequestParam Long studentUserId,
            @RequestBody CreateSessionRequestDto request) {

        // Validation
        Instant now = Instant.now();

        // Check if request is in the past
        if (request.getRequestedStart().isBefore(now)) {
            return ResponseEntity.badRequest()
                    .body("Cannot request sessions in the past");
        }

        // Check if request is more than 3 months in future
        Instant maxFutureDate = now.plus(MAX_DAYS_IN_FUTURE, ChronoUnit.DAYS);
        if (request.getRequestedStart().isAfter(maxFutureDate)) {
            return ResponseEntity.badRequest()
                    .body("Cannot request sessions more than 3 months in advance");
        }

        // Check minimum duration (1 hour)
        Duration duration = Duration.between(
                request.getRequestedStart(),
                request.getRequestedEnd()
        );
        if (duration.toMinutes() < MIN_DURATION_MINUTES) {
            return ResponseEntity.badRequest()
                    .body("Session must be at least 1 hour long");
        }

        // Check if tutor has conflicting accepted session
        boolean hasConflict = sessionRequestRepository.hasConflictingSession(
                request.getTutorUserId(),
                request.getRequestedStart(),
                request.getRequestedEnd()
        );

        if (hasConflict) {
            return ResponseEntity.badRequest()
                    .body("Tutor already has a session scheduled at this time");
        }

        // Create session request
        SessionRequest sessionRequest = new SessionRequest();
        sessionRequest.setStudentUserId(studentUserId);
        sessionRequest.setTutorUserId(request.getTutorUserId());
        sessionRequest.setRequestedStart(request.getRequestedStart());
        sessionRequest.setRequestedEnd(request.getRequestedEnd());
        sessionRequest.setStudentTimezone(
                request.getStudentTimezone() != null
                        ? request.getStudentTimezone()
                        : "America/Los_Angeles"
        );
        sessionRequest.setSubject(request.getSubject());
        sessionRequest.setMessage(request.getMessage());
        sessionRequest.setStatus(SessionRequestStatus.PENDING);

        // Get tutor's timezone from user profile (if exists)
        userRepository.findById(request.getTutorUserId())
                .ifPresent(tutor -> {
                    // Assuming you might add timezone field to User later
                    sessionRequest.setTutorTimezone("America/Los_Angeles");
                });

        SessionRequest saved = sessionRequestRepository.save(sessionRequest);

        return ResponseEntity.ok(toDto(saved));
    }

    /**
     * GET /api/session-requests/tutor/{tutorUserId}
     * Get all session requests for a tutor
     */
    @GetMapping("/tutor/{tutorUserId}")
    public ResponseEntity<List<SessionRequestDto>> getTutorSessionRequests(
            @PathVariable Long tutorUserId,
            @RequestParam(required = false) String status) {

        List<SessionRequest> requests;

        if (status != null && !status.isEmpty()) {
            SessionRequestStatus statusEnum = SessionRequestStatus.valueOf(status);
            requests = sessionRequestRepository
                    .findByTutorUserIdAndStatusOrderByRequestedStartAsc(
                            tutorUserId, statusEnum
                    );
        } else {
            requests = sessionRequestRepository
                    .findByTutorUserIdOrderByRequestedStartAsc(tutorUserId);
        }

        List<SessionRequestDto> dtos = requests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/session-requests/student/{studentUserId}
     * Get all session requests for a student
     */
    @GetMapping("/student/{studentUserId}")
    public ResponseEntity<List<SessionRequestDto>> getStudentSessionRequests(
            @PathVariable Long studentUserId) {

        List<SessionRequest> requests = sessionRequestRepository
                .findByStudentUserIdOrderByRequestedStartDesc(studentUserId);

        List<SessionRequestDto> dtos = requests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/session-requests/tutor/{tutorUserId}/pending
     * Get pending session requests for a tutor
     */
    @GetMapping("/tutor/{tutorUserId}/pending")
    public ResponseEntity<List<SessionRequestDto>> getPendingRequests(
            @PathVariable Long tutorUserId) {

        List<SessionRequest> requests = sessionRequestRepository
                .findByTutorUserIdAndStatusOrderByRequestedStartAsc(
                        tutorUserId, SessionRequestStatus.PENDING
                );

        List<SessionRequestDto> dtos = requests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/session-requests/tutor/{tutorUserId}/upcoming
     * Get upcoming accepted sessions for a tutor
     */
    @GetMapping("/tutor/{tutorUserId}/upcoming")
    public ResponseEntity<List<SessionRequestDto>> getUpcomingSessions(
            @PathVariable Long tutorUserId) {

        List<SessionRequest> requests = sessionRequestRepository
                .findUpcomingSessionsForTutor(tutorUserId, Instant.now());

        List<SessionRequestDto> dtos = requests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * PUT /api/session-requests/{requestId}/respond
     * Tutor responds to a session request (accept or decline)
     */
    @PutMapping("/{requestId}/respond")
    public ResponseEntity<?> respondToRequest(
            @PathVariable Long requestId,
            @RequestParam Long tutorUserId,
            @RequestBody RespondToSessionRequestDto response) {

        return sessionRequestRepository.findById(requestId)
                .map(request -> {
                    // Verify this is the tutor's request
                    if (!request.getTutorUserId().equals(tutorUserId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Not authorized to respond to this request");
                    }

                    // Can only respond to pending requests
                    if (request.getStatus() != SessionRequestStatus.PENDING) {
                        return ResponseEntity.badRequest()
                                .body("Can only respond to pending requests");
                    }

                    // Validate status (must be ACCEPTED or DECLINED)
                    if (response.getStatus() != SessionRequestStatus.ACCEPTED &&
                            response.getStatus() != SessionRequestStatus.DECLINED) {
                        return ResponseEntity.badRequest()
                                .body("Status must be ACCEPTED or DECLINED");
                    }

                    // If accepting, check for conflicts again
                    if (response.getStatus() == SessionRequestStatus.ACCEPTED) {
                        boolean hasConflict = sessionRequestRepository
                                .hasConflictingSession(
                                        tutorUserId,
                                        request.getRequestedStart(),
                                        request.getRequestedEnd()
                                );

                        if (hasConflict) {
                            return ResponseEntity.badRequest()
                                    .body("You already have a session scheduled at this time");
                        }
                    }

                    // Update request
                    request.setStatus(response.getStatus());
                    request.setTutorResponseMessage(response.getResponseMessage());
                    request.setRespondedAt(Instant.now());

                    SessionRequest saved = sessionRequestRepository.save(request);

                    return ResponseEntity.ok(toDto(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/session-requests/{requestId}
     * Cancel a session request (student only, for pending requests)
     */
    @DeleteMapping("/{requestId}")
    public ResponseEntity<?> cancelRequest(
            @PathVariable Long requestId,
            @RequestParam Long studentUserId) {

        return sessionRequestRepository.findById(requestId)
                .map(request -> {
                    // Verify this is the student's request
                    if (!request.getStudentUserId().equals(studentUserId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }

                    // Can only cancel pending requests
                    if (request.getStatus() != SessionRequestStatus.PENDING) {
                        return ResponseEntity.badRequest().build();
                    }

                    request.setStatus(SessionRequestStatus.CANCELLED);
                    sessionRequestRepository.save(request);

                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Accept a session request
     * POST /api/session-requests/{requestId}/accept
     */
    @PostMapping("/{requestId}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Long requestId) {
        try {
            SessionRequest request = sessionRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Session request not found"));

            // Validate current status
            if (request.getStatus() != SessionRequestStatus.PENDING) {
                return ResponseEntity.badRequest()
                        .body("Can only accept pending requests");
            }

            // Update status
            request.setStatus(SessionRequestStatus.ACCEPTED);
            request.setRespondedAt(Instant.now());
            sessionRequestRepository.save(request);

            return ResponseEntity.ok("Session request accepted");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to accept request: " + e.getMessage());
        }
    }

    /**
     * Decline a session request
     * POST /api/session-requests/{requestId}/decline
     */
    @PostMapping("/{requestId}/decline")
    public ResponseEntity<?> declineRequest(@PathVariable Long requestId) {
        try {
            SessionRequest request = sessionRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Session request not found"));

            // Validate current status
            if (request.getStatus() != SessionRequestStatus.PENDING) {
                return ResponseEntity.badRequest()
                        .body("Can only decline pending requests");
            }

            // Update status
            request.setStatus(SessionRequestStatus.DECLINED);
            request.setRespondedAt(Instant.now());
            sessionRequestRepository.save(request);

            return ResponseEntity.ok("Session request declined");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to decline request: " + e.getMessage());
        }
    }

    // Helper method to convert entity to DTO
    private SessionRequestDto toDto(SessionRequest request) {
        User student = userRepository.findById(request.getStudentUserId()).orElse(null);
        User tutor = userRepository.findById(request.getTutorUserId()).orElse(null);

        return new SessionRequestDto(
                request.getId(),
                request.getStudentUserId(),
                student != null ? student.getFirstName() : null,
                student != null ? student.getLastName() : null,
                student != null ? student.getEmail() : null,
                request.getTutorUserId(),
                tutor != null ? tutor.getFirstName() : null,
                tutor != null ? tutor.getLastName() : null,
                tutor != null ? tutor.getEmail() : null,
                request.getRequestedStart(),
                request.getRequestedEnd(),
                request.getStudentTimezone(),
                request.getTutorTimezone(),
                request.getSubject(),
                request.getMessage(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.getRespondedAt(),
                request.getTutorResponseMessage()
        );
    }
}