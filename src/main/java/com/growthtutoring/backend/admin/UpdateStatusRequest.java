package com.growthtutoring.backend.admin;

import com.growthtutoring.backend.user.UserStatus;

public class UpdateStatusRequest {
    private UserStatus status;

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}