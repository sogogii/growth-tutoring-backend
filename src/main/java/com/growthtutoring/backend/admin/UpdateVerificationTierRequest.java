package com.growthtutoring.backend.admin;

import com.growthtutoring.backend.tutor.VerificationTier;

public class UpdateVerificationTierRequest {
    private VerificationTier verificationTier;

    public VerificationTier getVerificationTier() {
        return verificationTier;
    }

    public void setVerificationTier(VerificationTier verificationTier) {
        this.verificationTier = verificationTier;
    }
}