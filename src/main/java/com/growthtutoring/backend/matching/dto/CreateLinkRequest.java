package com.growthtutoring.backend.matching.dto;

public class CreateLinkRequest {

    private Long studentUserId;
    private Long tutorUserId;

    public CreateLinkRequest() {
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
}