package com.growthtutoring.backend.session;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Represents a tutoring session request from a student to a tutor
 * All times are stored in UTC in the database
 */
@Entity
@Table(name = "session_requests")
public class SessionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_user_id", nullable = false)
    private Long studentUserId;

    @Column(name = "tutor_user_id", nullable = false)
    private Long tutorUserId;

    // Session timing (UTC)
    @Column(name = "requested_start", nullable = false)
    private Instant requestedStart;

    @Column(name = "requested_end", nullable = false)
    private Instant requestedEnd;

    // Timezone information
    @Column(name = "student_timezone", nullable = false)
    private String studentTimezone = "America/Los_Angeles";

    @Column(name = "tutor_timezone")
    private String tutorTimezone = "America/Los_Angeles";

    // Request details
    @Column(name = "subject")
    private String subject;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionRequestStatus status = SessionRequestStatus.PENDING;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "responded_at")
    private Instant respondedAt;

    // Tutor response
    @Column(name = "tutor_response_message", columnDefinition = "TEXT")
    private String tutorResponseMessage;

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentUserId() {
        return studentUserId;
    }

    public void setStudentUserId(Long studentUserId) {
        this.studentUserId = studentUserId;
    }

    public Long getTutorUserId() {
        return tutorUserId;
    }

    public void setTutorUserId(Long tutorUserId) {
        this.tutorUserId = tutorUserId;
    }

    public Instant getRequestedStart() {
        return requestedStart;
    }

    public void setRequestedStart(Instant requestedStart) {
        this.requestedStart = requestedStart;
    }

    public Instant getRequestedEnd() {
        return requestedEnd;
    }

    public void setRequestedEnd(Instant requestedEnd) {
        this.requestedEnd = requestedEnd;
    }

    public String getStudentTimezone() {
        return studentTimezone;
    }

    public void setStudentTimezone(String studentTimezone) {
        this.studentTimezone = studentTimezone;
    }

    public String getTutorTimezone() {
        return tutorTimezone;
    }

    public void setTutorTimezone(String tutorTimezone) {
        this.tutorTimezone = tutorTimezone;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SessionRequestStatus getStatus() {
        return status;
    }

    public void setStatus(SessionRequestStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(Instant respondedAt) {
        this.respondedAt = respondedAt;
    }

    public String getTutorResponseMessage() {
        return tutorResponseMessage;
    }

    public void setTutorResponseMessage(String tutorResponseMessage) {
        this.tutorResponseMessage = tutorResponseMessage;
    }
}