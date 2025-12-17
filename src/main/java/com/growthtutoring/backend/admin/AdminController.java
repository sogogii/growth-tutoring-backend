package com.growthtutoring.backend.admin;

import com.growthtutoring.backend.user.User;
import com.growthtutoring.backend.user.UserRepository;
import com.growthtutoring.backend.user.UserRole;
import com.growthtutoring.backend.user.UserStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ----- helper: load admin by id and check role -----
    private User requireAdmin(Long adminUserId) {
        if (adminUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin userId is required");
        }

        User me = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin user not found"));

        if (me.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
        }

        return me;
    }

    // 1) Get all users
    @GetMapping("/users")
    public List<User> listAllUsers(@RequestParam("adminUserId") Long adminUserId) {
        requireAdmin(adminUserId);
        return userRepository.findAll();
    }

    // 2) Update user status (PENDING / ACTIVE / SUSPENDED)
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<?> updateStatus(
            @RequestParam("adminUserId") Long adminUserId,
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest body
    ) {
        requireAdmin(adminUserId);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setStatus(body.getStatus());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    // 3) Update user role (STUDENT / TUTOR / ADMIN)
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<?> updateRole(
            @RequestParam("adminUserId") Long adminUserId,
            @PathVariable Long id,
            @RequestBody UpdateRoleRequest body
    ) {
        User me = requireAdmin(adminUserId);

        // optional: prevent admin from removing their own admin role
        if (me.getId().equals(id) && body.getRole() != UserRole.ADMIN) {
            return ResponseEntity.badRequest()
                    .body("Admin cannot remove their own ADMIN role.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setRole(body.getRole());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    // 4) Delete user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @RequestParam("adminUserId") Long adminUserId,
            @PathVariable Long id
    ) {
        requireAdmin(adminUserId);

        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ----- DTOs -----
    public static class UpdateStatusRequest {
        private UserStatus status;
        public UserStatus getStatus() { return status; }
        public void setStatus(UserStatus status) { this.status = status; }
    }

    public static class UpdateRoleRequest {
        private UserRole role;
        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }
    }
}
