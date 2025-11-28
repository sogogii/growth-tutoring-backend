package com.growthtutoring.backend.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://dev.growthtutoringhq.com"
})
public class UserProfileController {

    private final UserRepository userRepository;

    public UserProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET /api/users/{id} -> view profile
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private UserProfileDto toDto(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getUserUid(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getBirthday(),
                user.getProfileImageUrl(),
                user.getStatus().name(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }

    // PUT /api/users/{id} -> edit profile
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable Long id,
            @RequestBody UpdateUserProfileRequest req
    ) {
        return userRepository.findById(id)
                .map(user -> {
                    // editable fields: first/last name
                    user.setFirstName(req.getFirstName());
                    user.setLastName(req.getLastName());

                    // birthday
                    if (req.getBirthday() != null && !req.getBirthday().isBlank()) {
                        user.setBirthday(LocalDate.parse(req.getBirthday()));
                    }

                    // profile image URL
                    user.setProfileImageUrl(req.getProfileImageUrl());

                    // userUid change with 30-day rule
                    String newUserUid = req.getUserUid();
                    if (newUserUid != null && !newUserUid.equals(user.getUserUid())) {

                        // 1) must be unique
                        if (userRepository.existsByUserUid(newUserUid)) {
                            return ResponseEntity.badRequest().body("User ID already in use");
                        }

                        // 2) 30-day cooldown
                        Instant now = Instant.now();
                        Instant lastChange = user.getLastUserUidChangeAt();

                        if (lastChange != null) {
                            long days = Duration.between(lastChange, now).toDays();
                            if (days < 30) {
                                return ResponseEntity.badRequest()
                                        .body("You can change your User ID only once every 30 days.");
                            }
                        }

                        user.setUserUid(newUserUid);
                        user.setLastUserUidChangeAt(now);
                    }

                    User saved = userRepository.save(user);
                    return ResponseEntity.ok(toDto(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
