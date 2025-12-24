package com.growthtutoring.backend.session;

/**
 * Status of a session request
 */
public enum SessionRequestStatus {
    PENDING,    // Waiting for tutor response
    ACCEPTED,   // Tutor accepted the request
    DECLINED,   // Tutor declined the request
    CANCELLED   // Student cancelled the request
}