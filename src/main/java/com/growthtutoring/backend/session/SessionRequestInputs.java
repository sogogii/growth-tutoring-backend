package com.growthtutoring.backend.session;

import java.time.Instant;

/**
 * Request DTO for creating a new session request
 */
class CreateSessionRequestDto {
    private Long tutorUserId;
    private Instant requestedStart;  // UTC timestamp
    private Instant requestedEnd;    // UTC timestamp
    private String studentTimezone;  // e.g., "America/Los_Angeles"
    private String subject;
    private String message;

    // Getters and Setters
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
}

/**
 * Request DTO for tutor responding to a session request
 */
class RespondToSessionRequestDto {
    private SessionRequestStatus status;  // ACCEPTED or DECLINED
    private String responseMessage;

    // Getters and Setters
    public SessionRequestStatus getStatus() {
        return status;
    }

    public void setStatus(SessionRequestStatus status) {
        this.status = status;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}