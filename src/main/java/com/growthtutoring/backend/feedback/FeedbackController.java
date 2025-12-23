package com.growthtutoring.backend.feedback;

import com.growthtutoring.backend.auth.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final EmailService emailService;
    private final RecaptchaService recaptchaService;

    // File size limits
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_TOTAL_SIZE = 20 * 1024 * 1024; // 20MB

    public FeedbackController(EmailService emailService, RecaptchaService recaptchaService) {
        this.emailService = emailService;
        this.recaptchaService = recaptchaService;
    }

    @PostMapping
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequest request) {
        // Validate required fields
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        if (request.getComment() == null || request.getComment().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Feedback comment is required");
        }

        // Verify reCAPTCHA
        if (!recaptchaService.verifyRecaptcha(request.getRecaptchaToken())) {
            return ResponseEntity.badRequest().body("reCAPTCHA verification failed. Please try again.");
        }

        // Validate attachments if present
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            long totalSize = 0;
            for (FileAttachment attachment : request.getAttachments()) {
                if (attachment.getSize() > MAX_FILE_SIZE) {
                    return ResponseEntity.badRequest().body(
                            "File too large: " + attachment.getName() + ". Maximum size per file: 10MB"
                    );
                }
                totalSize += attachment.getSize();
            }

            if (totalSize > MAX_TOTAL_SIZE) {
                return ResponseEntity.badRequest().body("Total file size exceeds 20MB limit");
            }
        }

        try {
            // Send feedback email to info@growthtutoringhq.com
            emailService.sendFeedbackEmail(
                    request.getName(),
                    request.getEmail(),
                    request.getComment(),
                    request.getAttachments()
            );

            return ResponseEntity.ok("Feedback submitted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send feedback: " + e.getMessage());
        }
    }
}