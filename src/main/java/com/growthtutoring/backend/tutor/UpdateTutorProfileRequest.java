package com.growthtutoring.backend.tutor;

import java.math.BigDecimal;

/**
 * Request DTO for updating tutor profile information
 * Used when tutors edit their profile in the My Profile page
 */
public class UpdateTutorProfileRequest {

    private String subjectLabel;   // e.g., "K-12 Math, Physics, Chemistry"
    private BigDecimal hourlyRate;
    private String teachingMethod; // "ONLINE", "IN_PERSON", or "HYBRID"
    private String headline;       // Brief summary (max 255 chars) for tutors listing page
    private String bio;            // Detailed bio for tutor profile page

    // Getters and Setters

    public String getSubjectLabel() {
        return subjectLabel;
    }

    public void setSubjectLabel(String subjectLabel) {
        this.subjectLabel = subjectLabel;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getTeachingMethod() {
        return teachingMethod;
    }

    public void setTeachingMethod(String teachingMethod) {
        this.teachingMethod = teachingMethod;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}