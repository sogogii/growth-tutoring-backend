package com.growthtutoring.backend.tutor;

import com.growthtutoring.backend.student.Student;
import com.growthtutoring.backend.student.StudentRepository;
import com.growthtutoring.backend.user.User;
import com.growthtutoring.backend.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tutors")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://dev.growthtutoringhq.com"
})
public class TutorReviewController {

    private final TutorRepository tutorRepository;
    private final TutorReviewRepository reviewRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public TutorReviewController(
            TutorRepository tutorRepository,
            TutorReviewRepository reviewRepository,
            StudentRepository studentRepository,
            UserRepository userRepository
    ) {
        this.tutorRepository = tutorRepository;
        this.reviewRepository = reviewRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    // ---------- GET: /api/tutors/user/{userId}/reviews ----------
    @GetMapping("/user/{userId}/reviews")
    public ResponseEntity<List<TutorReviewDto>> getReviewsForTutor(
            @PathVariable Long userId
    ) {
        // find tutor row from user_id
        Tutor tutor = tutorRepository.findByUserId(userId).orElse(null);
        if (tutor == null) {
            return ResponseEntity.notFound().build();
        }

        List<TutorReview> reviews = reviewRepository.findByTutorId(tutor.getId());

        List<TutorReviewDto> dtos = reviews.stream().map(r -> {
            Student s = r.getStudent();
            User u = (s != null)
                    ? userRepository.findById(s.getUserId()).orElse(null)
                    : null;

            Long studentUserId = (u != null) ? u.getId() : null;
            String firstName = (u != null) ? u.getFirstName() : null;
            String lastName  = (u != null) ? u.getLastName()  : null;

            return new TutorReviewDto(
                    r.getId(),
                    r.getRating(),
                    r.getComment(),
                    r.getCreatedAt(),
                    studentUserId,
                    firstName,
                    lastName
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);   // 200 with [] if none
    }

    // ---------- POST: /api/tutors/user/{userId}/reviews ----------
    @PostMapping("/user/{userId}/reviews")
    public ResponseEntity<?> createOrUpdateReview(
            @PathVariable Long userId,            // tutor's user_id
            @RequestBody TutorReviewRequest request
    ) {
        // 1) find tutor by userId
        Tutor tutor = tutorRepository.findByUserId(userId).orElse(null);
        if (tutor == null) {
            return ResponseEntity.notFound().build();
        }

        // 2) find student by userId from body (current logged-in student)
        if (request.getUserId() == null) {
            return ResponseEntity.badRequest().body("userId is required");
        }

        Student student = studentRepository.findByUserId(request.getUserId())
                .orElse(null);
        if (student == null) {
            return ResponseEntity.badRequest().body("Student record not found for this user");
        }

        BigDecimal rating = request.getRating();
        if (rating == null ||
                rating.compareTo(BigDecimal.ONE) < 0 ||
                rating.compareTo(new BigDecimal("5.0")) > 0) {
            return ResponseEntity.badRequest().body("Rating must be between 1.0 and 5.0");
        }

        // 3) find existing review by this student for this tutor
        Optional<TutorReview> existingOpt =
                reviewRepository.findByTutorIdAndStudentId(tutor.getId(), student.getId());

        TutorReview review;
        if (existingOpt.isPresent()) {
            review = existingOpt.get();
            review.setRating(rating);
            review.setComment(request.getComment());
        } else {
            review = new TutorReview();
            review.setTutor(tutor);
            review.setStudent(student);
            review.setRating(rating);
            review.setComment(request.getComment());
        }

        reviewRepository.save(review);

        // 4) recompute avg + count and update tutor row
        BigDecimal avg = reviewRepository.computeAverageRating(tutor.getId());
        long count = reviewRepository.countByTutorId(tutor.getId());

        tutor.setRatingAvg(avg);
        tutor.setRatingCount((int) count);
        tutorRepository.save(tutor);

        return ResponseEntity.ok("Review saved");
    }

    @DeleteMapping("/user/{userId}/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long userId,
            @PathVariable Long reviewId,
            @RequestParam("studentUserId") Long studentUserId
    ) {
        // find tutor by userId
        Tutor tutor = tutorRepository.findByUserId(userId).orElse(null);
        if (tutor == null) {
            return ResponseEntity.notFound().build();
        }

        // find the review
        Optional<TutorReview> opt = reviewRepository.findById(reviewId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TutorReview review = opt.get();

        // security: ensure this review belongs to this tutor
        if (!review.getTutor().getId().equals(tutor.getId())) {
            return ResponseEntity.status(403).body("Review does not belong to this tutor");
        }

        // security: ensure this review belongs to this student
        Student student = review.getStudent();
        if (student == null || !student.getUserId().equals(studentUserId)) {
            return ResponseEntity.status(403).body("You can only delete your own review");
        }

        reviewRepository.delete(review);

        // recompute avg + count
        BigDecimal avg = reviewRepository.computeAverageRating(tutor.getId());
        long count = reviewRepository.countByTutorId(tutor.getId());
        tutor.setRatingAvg(avg);
        tutor.setRatingCount((int) count);
        tutorRepository.save(tutor);

        return ResponseEntity.ok("Review deleted");
    }

}
