package com.growthtutoring.backend.auth;

import com.growthtutoring.backend.auth.dto.*;
import com.growthtutoring.backend.user.User;
import com.growthtutoring.backend.user.UserRepository;
import com.growthtutoring.backend.user.UserRole;
import com.growthtutoring.backend.user.UserStatus;
import com.growthtutoring.backend.tutor.Tutor;
import com.growthtutoring.backend.tutor.TutorRepository;
import com.growthtutoring.backend.student.Student;
import com.growthtutoring.backend.student.StudentRepository;
import com.growthtutoring.backend.tutor.TeachingMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://dev.growthtutoringhq.com"
})
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          TutorRepository tutorRepository,
                          StudentRepository studentRepository,
                          EmailService emailService,
                          PasswordResetService passwordResetService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tutorRepository = tutorRepository;
        this.studentRepository = studentRepository;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
    }

    // ==================== EMAIL VERIFICATION ENDPOINTS ====================

    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerification(@RequestBody SendVerificationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        try {
            emailService.sendVerificationEmail(request.getEmail());
            return ResponseEntity.ok("Verification code sent");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send verification email");
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest request) {
        boolean isValid = emailService.verifyCode(request.getEmail(), request.getCode());

        if (isValid) {
            return ResponseEntity.ok("Code verified");
        } else {
            return ResponseEntity.status(400).body("Invalid or expired code");
        }
    }

    // ==================== SIGNUP ENDPOINT ====================

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        if (userRepository.existsByUserUid(request.getUserUid())) {
            return ResponseEntity.badRequest().body("User ID already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setUserUid(request.getUserUid());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthday(LocalDate.parse(request.getBirthday()));
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        user.setEmailVerified(true); // Set to true after verification

        UserRole role = UserRole.valueOf(request.getRole().toUpperCase());
        user.setRole(role);

        if (role == UserRole.TUTOR) {
            user.setStatus(UserStatus.PENDING);
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }

        User savedUser = userRepository.save(user);

        if (role == UserRole.TUTOR) {
            Tutor tutor = new Tutor();
            tutor.setUserId(savedUser.getId());
            tutor.setJoinedAt(java.time.LocalDate.now());
            tutor.setSubjectLabel(request.getSubjectLabel());
            tutor.setRatingAvg(null);
            tutor.setRatingCount(0);
            tutor.setYearsExperience(0);
            tutor.setEducation(null);
            tutor.setTeachingMethod(TeachingMethod.ONLINE);
            tutor.setHeadline(null);
            tutor.setBio(null);
            tutor.setHourlyRate(java.math.BigDecimal.ZERO);

            tutorRepository.save(tutor);
        } else if (role == UserRole.STUDENT) {
            Student student = new Student();
            student.setUserId(savedUser.getId());
            studentRepository.save(student);
        }

        AuthResponse response = new AuthResponse(
                savedUser.getId(),
                savedUser.getUserUid(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole().name()
        );

        return ResponseEntity.ok(response);
    }

    // ==================== LOGIN ENDPOINT ====================

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            return ResponseEntity.status(403).body("Account is suspended");
        }

        AuthResponse response = new AuthResponse(
                user.getId(),
                user.getUserUid(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }

    // ==================== PASSWORD RESET ENDPOINTS ====================

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody RequestPasswordResetRequest request) {
        // Check if email exists
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            // For security, don't reveal if email exists or not
            return ResponseEntity.ok("If the email exists, a reset code has been sent");
        }

        try {
            passwordResetService.sendPasswordResetEmail(request.getEmail());
            return ResponseEntity.ok("Password reset code sent");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send reset code");
        }
    }

    @PostMapping("/verify-reset-token")
    public ResponseEntity<?> verifyResetToken(@RequestBody ResetPasswordRequest request) {
        boolean isValid = passwordResetService.verifyResetToken(request.getEmail(), request.getToken());

        if (isValid) {
            return ResponseEntity.ok("Token verified");
        } else {
            return ResponseEntity.status(400).body("Invalid or expired token");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        // Verify token first
        boolean isValid = passwordResetService.verifyResetToken(request.getEmail(), request.getToken());

        if (!isValid) {
            return ResponseEntity.status(400).body("Invalid or expired token");
        }

        // Find user
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        passwordResetService.markTokenAsUsed(request.getEmail(), request.getToken());

        return ResponseEntity.ok("Password reset successfully");
    }
}