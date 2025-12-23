package com.growthtutoring.backend.auth;

import com.growthtutoring.backend.feedback.FileAttachment;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository verificationRepository;

    public EmailService(JavaMailSender mailSender,
                        EmailVerificationRepository verificationRepository) {
        this.mailSender = mailSender;
        this.verificationRepository = verificationRepository;
    }

    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6-digit code
        return String.valueOf(code);
    }

    public void sendVerificationEmail(String toEmail) {
        String code = generateVerificationCode();

        // Save to database
        EmailVerification verification = new EmailVerification();
        verification.setEmail(toEmail);
        verification.setVerificationCode(code);
        verification.setExpiresAt(Instant.now().plus(3, ChronoUnit.MINUTES)); // 3 min expiry
        verification.setVerified(false);

        verificationRepository.save(verification);

        // Print to console (for debugging)
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("📧 Email Verification Code");
        System.out.println("Email: " + toEmail);
        System.out.println("Code: " + code);
        System.out.println("Expires: " + verification.getExpiresAt());
        System.out.println("═══════════════════════════════════════════════════\n");

        // Send actual email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Growth Tutoring - Email Verification");
            message.setText(
                    "Welcome to Growth Tutoring!\n\n" +
                            "Your verification code is: " + code + "\n\n" +
                            "This code will expire in 3 minutes.\n\n" +
                            "If you didn't request this code, please ignore this email.\n\n" +
                            "Best regards,\n" +
                            "Growth Tutoring Team"
            );

            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean verifyCode(String email, String code) {
        var verification = verificationRepository
                .findByEmailAndVerificationCodeAndVerifiedFalse(email, code);

        if (verification.isEmpty()) {
            return false;
        }

        EmailVerification v = verification.get();

        // Check if expired
        if (Instant.now().isAfter(v.getExpiresAt())) {
            return false;
        }

        // Mark as verified
        v.setVerified(true);
        verificationRepository.save(v);

        return true;
    }

    /**
     * Send feedback email to info@growthtutoringhq.com with optional file attachments
     */
    public void sendFeedbackEmail(String name, String email, String comment, List<FileAttachment> attachments) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // true = multipart

            helper.setTo("info@growthtutoringhq.com");
            helper.setSubject("New Feedback from Website");
            helper.setReplyTo(email);

            // Build email body
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("New feedback received from the website:\n\n");
            emailBody.append("Name: ").append(name).append("\n");
            emailBody.append("Email: ").append(email).append("\n\n");
            emailBody.append("Feedback:\n").append(comment).append("\n");

            if (attachments != null && !attachments.isEmpty()) {
                emailBody.append("\n").append(attachments.size())
                        .append(" file(s) attached\n");
            }

            emailBody.append("\n---\n");
            emailBody.append("This is an automated message from Growth Tutoring website.");

            helper.setText(emailBody.toString());

            // Add attachments if present
            if (attachments != null && !attachments.isEmpty()) {
                for (FileAttachment attachment : attachments) {
                    try {
                        // Decode base64 data
                        byte[] fileData = Base64.getDecoder().decode(attachment.getData());

                        // Create data source
                        ByteArrayDataSource dataSource = new ByteArrayDataSource(
                                fileData,
                                attachment.getType()
                        );

                        // Add attachment
                        helper.addAttachment(attachment.getName(), dataSource);

                        System.out.println("📎 Added attachment: " + attachment.getName() +
                                " (" + formatFileSize(attachment.getSize()) + ")");
                    } catch (Exception e) {
                        System.err.println("❌ Failed to attach file: " + attachment.getName());
                        e.printStackTrace();
                    }
                }
            }

            mailSender.send(message);

            System.out.println("\n═══════════════════════════════════════════════════");
            System.out.println("📨 Feedback Email Sent");
            System.out.println("From: " + name);
            System.out.println("Email: " + email);
            if (attachments != null && !attachments.isEmpty()) {
                System.out.println("Attachments: " + attachments.size() + " file(s)");
            }
            System.out.println("═══════════════════════════════════════════════════\n");

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send feedback email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send feedback email", e);
        }
    }

    private String formatFileSize(Long bytes) {
        if (bytes == null) return "0 B";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
}