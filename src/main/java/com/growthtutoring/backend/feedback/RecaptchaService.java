package com.growthtutoring.backend.feedback;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret.key}")
    private String secretKey;

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyRecaptcha(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey);
            params.add("response", token);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    RECAPTCHA_VERIFY_URL,
                    request,
                    Map.class
            );

            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("success")) {
                Boolean success = (Boolean) body.get("success");

                System.out.println("\n═══════════════════════════════════════════════════");
                System.out.println("🔒 reCAPTCHA Verification");
                System.out.println("Success: " + success);
                if (body.containsKey("score")) {
                    System.out.println("Score: " + body.get("score"));
                }
                if (body.containsKey("error-codes")) {
                    System.out.println("Errors: " + body.get("error-codes"));
                }
                System.out.println("═══════════════════════════════════════════════════\n");

                return success != null && success;
            }

            return false;
        } catch (Exception e) {
            System.err.println("❌ reCAPTCHA verification failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}