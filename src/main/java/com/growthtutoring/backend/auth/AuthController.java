package com.growthtutoring.backend.auth;

import com.growthtutoring.backend.auth.dto.AuthResponse;
import com.growthtutoring.backend.auth.dto.LoginRequest;
import com.growthtutoring.backend.auth.dto.SignupRequest;
import com.growthtutoring.backend.auth.dto.SendVerificationRequest;
import com.growthtutoring.backend.auth.dto.VerifyCodeRequest;
import com.growthtutoring.backend.user.User;
import com.growthtutoring.backend.user.UserRepository;
import com.growthtutoring.backend.user.UserRole;
import com.growthtutoring.backend.user.UserStatus;
import com.growthtutoring.backend.tutor.Tutor;
import com.growthtutoring.backend.tutor.TutorRepository;
import com.growthtutoring.backend.tutor.VerificationTier;
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

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          TutorRepository tutorRepository,
                          StudentRepository studentRepository,
                          EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tutorRepository = tutorRepository;
        this.studentRepository = studentRepository;
        this.emailService = emailService;
    }

    // ==================== EMAIL VERIFICATION ENDPOINTS ====================

    /**
     * POST /api/auth/send-verification
     * Sends a 6-digit verification code to the provided email
     */
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerification(@RequestBody SendVerificationRequest request) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("Email already in use");
            }

            emailService.sendVerificationEmail(request.getEmail());
            return ResponseEntity.ok("Verification code sent to " + request.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send verification email: " + e.getMessage());
        }
    }

    /**
     * POST /api/auth/verify-code
     * Verifies the 6-digit code sent to the email
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest request) {
        boolean isValid = emailService.verifyCode(request.getEmail(), request.getCode());

        if (isValid) {
            return ResponseEntity.ok("Email verified successfully");
        } else {
            return ResponseEntity.status(400).body("Invalid or expired verification code");
        }
    }

    // ==================== SIGNUP ENDPOINT ====================

    /**
     * POST /api/auth/signup
     * Creates a new user account (after email verification)
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        // 1) Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        // 2) Check if userUid already exists
        if (userRepository.existsByUserUid(request.getUserUid())) {
            return ResponseEntity.badRequest().body("User ID already in use");
        }

        // 3) Create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setUserUid(request.getUserUid());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthday(LocalDate.parse(request.getBirthday()));

        UserRole role = UserRole.valueOf(request.getRole().toUpperCase());
        user.setRole(role);

        // Set status based on role
        if (role == UserRole.TUTOR) {
            user.setStatus(UserStatus.PENDING);
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }

        // Mark email as verified (since they passed verification step)
        user.setEmailVerified(true);

        User savedUser = userRepository.save(user);

        // 4) Create role-specific records
        if (role == UserRole.TUTOR) {
            Tutor tutor = new Tutor();
            tutor.setUserId(savedUser.getId());
            tutor.setJoinedAt(LocalDate.now());
            tutor.setSubjectLabel(request.getSubjectLabel());
            tutor.setRatingAvg(null);
            tutor.setRatingCount(0);
            tutor.setYearsExperience(0);
            tutor.setEducation(null);
            tutor.setTeachingMethod(TeachingMethod.ONLINE);
            tutor.setHeadline(null);
            tutor.setBio(null);
            tutor.setHourlyRate(java.math.BigDecimal.ZERO);
            tutor.setVerificationTier(VerificationTier.TIER_1);

            tutorRepository.save(tutor);
        } else if (role == UserRole.STUDENT) {
            Student student = new Student();
            student.setUserId(savedUser.getId());
            studentRepository.save(student);
        }

        // 5) Return response
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

    /**
     * POST /api/auth/login
     * Authenticates a user and returns their information
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Find user by email
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        User user = optionalUser.get();

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        // Check if account is suspended
        if (user.getStatus() == UserStatus.SUSPENDED) {
            return ResponseEntity.status(403).body("Account is suspended");
        }

        // Return user information
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
}