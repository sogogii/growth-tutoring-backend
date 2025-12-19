package com.growthtutoring.backend.auth;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        verification.setExpiresAt(Instant.now().plus(3, ChronoUnit.MINUTES)); // 3 min expiry ← CHANGED
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
                            "This code will expire in 3 minutes.\n\n" +  // ← CHANGED
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
}