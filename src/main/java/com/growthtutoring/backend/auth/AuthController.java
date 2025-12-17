package com.growthtutoring.backend.auth;

import com.growthtutoring.backend.auth.dto.AuthResponse;
import com.growthtutoring.backend.auth.dto.LoginRequest;
import com.growthtutoring.backend.auth.dto.SignupRequest;
import com.growthtutoring.backend.tutor.VerificationTier;
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
import java.util.UUID;

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

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          TutorRepository tutorRepository,
                          StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tutorRepository = tutorRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        // 1) check email
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        // 2) check chosen user id (userUid)
        if (userRepository.existsByUserUid(request.getUserUid())) {
            return ResponseEntity.badRequest().body("User ID already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // use the userUid typed by the user
        user.setUserUid(request.getUserUid());

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthday(LocalDate.parse(request.getBirthday()));
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        UserRole role = UserRole.valueOf(request.getRole().toUpperCase());
        user.setRole(role);

        if (role == UserRole.TUTOR) {
            user.setStatus(UserStatus.PENDING);
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }

        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);


        if (role == UserRole.TUTOR) {
            Tutor tutor = new Tutor();
            tutor.setUserId(savedUser.getId());
            tutor.setJoinedAt(java.time.LocalDate.now());

            // subject_label from signup (multi-select joined as string)
            tutor.setSubjectLabel(request.getSubjectLabel());

            // You can tune these defaults later or extend the form
            tutor.setRatingAvg(null);
            tutor.setRatingCount(0);
            tutor.setYearsExperience(0);
            tutor.setEducation(null);
            tutor.setTeachingMethod(TeachingMethod.ONLINE); // default for now
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

    private String generateUserUid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
