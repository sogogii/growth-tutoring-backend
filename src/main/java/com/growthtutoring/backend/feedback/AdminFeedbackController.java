package com.growthtutoring.backend.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/feedback")
public class AdminFeedbackController {

    private final FeedbackRepository feedbackRepository;

    public AdminFeedbackController(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * Get all feedback with pagination, filtering, and search
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllFeedback(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String search
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Feedback> feedbackPage;

            // Apply filters
            if (search != null && !search.trim().isEmpty()) {
                feedbackPage = feedbackRepository.searchFeedback(search.trim(), pageable);
            } else if (status != null && isRead != null) {
                FeedbackStatus feedbackStatus = FeedbackStatus.valueOf(status.toUpperCase());
                feedbackPage = feedbackRepository.findByStatusAndIsRead(feedbackStatus, isRead, pageable);
            } else if (status != null) {
                FeedbackStatus feedbackStatus = FeedbackStatus.valueOf(status.toUpperCase());
                feedbackPage = feedbackRepository.findByStatus(feedbackStatus, pageable);
            } else if (isRead != null) {
                feedbackPage = feedbackRepository.findByIsRead(isRead, pageable);
            } else {
                feedbackPage = feedbackRepository.findAllByOrderByCreatedAtDesc(pageable);
            }

            // Convert to DTOs
            Page<FeedbackDTO> dtoPage = feedbackPage.map(FeedbackDTO::new);

            Map<String, Object> response = new HashMap<>();
            response.put("feedback", dtoPage.getContent());
            response.put("currentPage", dtoPage.getNumber());
            response.put("totalItems", dtoPage.getTotalElements());
            response.put("totalPages", dtoPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Get feedback statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", feedbackRepository.count());
            stats.put("new", feedbackRepository.countByStatus(FeedbackStatus.NEW));
            stats.put("inProgress", feedbackRepository.countByStatus(FeedbackStatus.IN_PROGRESS));
            stats.put("resolved", feedbackRepository.countByStatus(FeedbackStatus.RESOLVED));
            stats.put("archived", feedbackRepository.countByStatus(FeedbackStatus.ARCHIVED));
            stats.put("unread", feedbackRepository.countByIsRead(false));

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Get single feedback by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedbackById(@PathVariable Long id) {
        try {
            Feedback feedback = feedbackRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Feedback not found"));

            return ResponseEntity.ok(new FeedbackDTO(feedback));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Feedback not found");
        }
    }

    /**
     * Mark feedback as read/unread
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request
    ) {
        try {
            Feedback feedback = feedbackRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Feedback not found"));

            feedback.setIsRead(request.get("isRead"));
            feedbackRepository.save(feedback);

            return ResponseEntity.ok(new FeedbackDTO(feedback));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Feedback not found");
        }
    }

    /**
     * Update feedback status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        try {
            Feedback feedback = feedbackRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Feedback not found"));

            FeedbackStatus newStatus = FeedbackStatus.valueOf(request.get("status").toUpperCase());
            feedback.setStatus(newStatus);
            feedbackRepository.save(feedback);

            return ResponseEntity.ok(new FeedbackDTO(feedback));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Feedback not found");
        }
    }

    /**
     * Update admin notes
     */
    @PatchMapping("/{id}/notes")
    public ResponseEntity<?> updateNotes(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        try {
            Feedback feedback = feedbackRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Feedback not found"));

            feedback.setAdminNotes(request.get("notes"));
            feedbackRepository.save(feedback);

            return ResponseEntity.ok(new FeedbackDTO(feedback));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Feedback not found");
        }
    }

    /**
     * Delete feedback
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        try {
            feedbackRepository.deleteById(id);
            return ResponseEntity.ok("Feedback deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Feedback not found");
        }
    }
}