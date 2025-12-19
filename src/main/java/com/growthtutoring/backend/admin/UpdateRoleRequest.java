package com.growthtutoring.backend.admin;

import com.growthtutoring.backend.user.UserRole;

public class UpdateRoleRequest {
    private UserRole role;

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}