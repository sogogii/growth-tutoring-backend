package com.growthtutoring.backend.matching.dto;

import com.growthtutoring.backend.user.User;

public class SimpleUserDto {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String userUid;
    private String profileImageUrl;

    public SimpleUserDto() {}

    // Convert from your User entity
    public SimpleUserDto(User user) {
        this.userId = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.userUid = user.getUserUid();
        this.profileImageUrl = user.getProfileImageUrl();
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

    public String getEmail() {
        return email;
    }

    public String getUserUid() {
        return userUid;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}