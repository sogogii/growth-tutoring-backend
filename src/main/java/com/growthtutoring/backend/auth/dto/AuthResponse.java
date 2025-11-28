package com.growthtutoring.backend.auth.dto;

public class AuthResponse {

    private Long userId;
    private String userUid;
    private String email;
    private String firstName;
    private String lastName;
    private String role;

    public AuthResponse(Long userId, String userUid, String email,
                        String firstName, String lastName, String role) {
        this.userId = userId;
        this.userUid = userUid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public Long getUserId() { return userId; }
    public String getUserUid() { return userUid; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
}
