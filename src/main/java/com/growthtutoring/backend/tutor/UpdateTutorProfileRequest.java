package com.growthtutoring.backend.tutor;

import java.math.BigDecimal;

/**
 * Request DTO for updating tutor profile
 * Used in PUT /api/tutors/user/{userId}
 */
public class UpdateTutorProfileRequest {
    private String subjectLabel;
    private BigDecimal hourlyRate;
    private String teachingMethod;
    private String headline;
    private String bio;
    private WeeklySchedule weeklySchedule;  // ADDED

    // Constructors
    public UpdateTutorProfileRequest() {}

    public UpdateTutorProfileRequest(String subjectLabel, BigDecimal hourlyRate,
                                     String teachingMethod, String headline,
                                     String bio, WeeklySchedule weeklySchedule) {
        this.subjectLabel = subjectLabel;
        this.hourlyRate = hourlyRate;
        this.teachingMethod = teachingMethod;
        this.headline = headline;
        this.bio = bio;
        this.weeklySchedule = weeklySchedule;
    }

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

    public WeeklySchedule getWeeklySchedule() {
        return weeklySchedule;
    }

    public void setWeeklySchedule(WeeklySchedule weeklySchedule) {
        this.weeklySchedule = weeklySchedule;
    }
}