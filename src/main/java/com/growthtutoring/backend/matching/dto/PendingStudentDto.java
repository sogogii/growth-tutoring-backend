package com.growthtutoring.backend.matching.dto;

public class PendingStudentDto {

    private Long linkId;   // id of StudentTutorLink
    private Long userId;   // student's user id
    private String firstName;
    private String lastName;
    private String userUid;
    private String email;

    public PendingStudentDto() {}

    public PendingStudentDto(Long linkId, Long userId,
                             String firstName, String lastName,
                             String userUid, String email) {
        this.linkId = linkId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userUid = userUid;
        this.email = email;
    }

    public Long getLinkId() {
        return linkId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserUid() {
        return userUid;
    }

    public String getEmail() {
        return email;
    }
}