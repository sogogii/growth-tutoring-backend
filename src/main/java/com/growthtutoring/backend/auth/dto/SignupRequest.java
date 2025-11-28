package com.growthtutoring.backend.auth.dto;

public class SignupRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String birthday; // "2002-02-01"
    private String role;     // "TUTOR", "STUDENT", "ADMIN"
    private String userUid;
    private String subjectLabel;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUserUid() { return userUid; }
    public void setUserUid(String userUid) { this.userUid = userUid; }

    public String getSubjectLabel() { return subjectLabel; }
    public void setSubjectLabel(String subjectLabel) { this.subjectLabel = subjectLabel; }

}
