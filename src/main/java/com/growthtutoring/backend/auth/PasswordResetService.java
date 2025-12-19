package com.growthtutoring.backend.auth;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
public class PasswordResetService {

    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository tokenRepository;

    public PasswordResetService(JavaMailSender mailSender,
                                PasswordResetTokenRepository tokenRepository) {
        this.mailSender = mailSender;
        this.tokenRepository = tokenRepository;
    }

    public String generateResetToken() {
        Random random = new Random();
        int token = 100000 + random.nextInt(900000); // 6-digit code
        return String.valueOf(token);
    }

    public void sendPasswordResetEmail(String toEmail) {
        String token = generateResetToken();

        // Save token to database
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(toEmail);
        resetToken.setResetToken(token);
        resetToken.setExpiresAt(Instant.now().plus(3, ChronoUnit.MINUTES)); // 3 min expiry ← CHANGED
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        // Print to console for debugging (keep this)
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("🔑 Password Reset Request");
        System.out.println("Email: " + toEmail);
        System.out.println("Reset Code: " + token);
        System.out.println("Expires: " + resetToken.getExpiresAt());
        System.out.println("═══════════════════════════════════════════════════\n");

        // Send actual email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Growth Tutoring - Password Reset");
            message.setText(
                    "You requested a password reset.\n\n" +
                            "Your password reset code is: " + token + "\n\n" +
                            "This code will expire in 3 minutes.\n\n" +  // ← CHANGED
                            "If you didn't request this, please ignore this email.\n\n" +
                            "Best regards,\n" +
                            "Growth Tutoring Team"
            );

            mailSender.send(message);
            System.out.println("✅ Password reset email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean verifyResetToken(String email, String token) {
        var resetToken = tokenRepository
                .findByEmailAndResetTokenAndUsedFalse(email, token);

        if (resetToken.isEmpty()) {
            return false;
        }

        PasswordResetToken rt = resetToken.get();

        // Check if expired
        if (Instant.now().isAfter(rt.getExpiresAt())) {
            return false;
        }

        return true;
    }

    public void markTokenAsUsed(String email, String token) {
        var resetToken = tokenRepository
                .findByEmailAndResetTokenAndUsedFalse(email, token);

        if (resetToken.isPresent()) {
            PasswordResetToken rt = resetToken.get();
            rt.setUsed(true);
            tokenRepository.save(rt);
        }
    }
}