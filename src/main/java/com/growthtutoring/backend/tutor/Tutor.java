package com.growthtutoring.backend.tutor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tutors")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "rating_avg", precision = 3, scale = 2)
    private BigDecimal ratingAvg;

    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;

    @Column(name = "joined_at", nullable = false)
    private LocalDate joinedAt;

    @Column(name = "subject_label", nullable = false, length = 100)
    private String subjectLabel;

    @Column(name = "years_experience", nullable = false)
    private Integer yearsExperience = 0;

    @Column(name = "education", length = 255)
    private String education;

    @Enumerated(EnumType.STRING)
    @Column(name = "teaching_method", nullable = false, length = 20)
    private TeachingMethod teachingMethod;

    @Column(name = "headline", length = 255)
    private String headline;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "hourly_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal hourlyRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_tier", length = 20)
    private VerificationTier verificationTier;

    // NEW: Weekly schedule stored as JSON
    @Column(name = "weekly_schedule", columnDefinition = "JSON")
    private String weeklyScheduleJson;

    // Transient field for easy access (not stored in DB)
    @Transient
    private WeeklySchedule weeklySchedule;

    // Helper for JSON conversion
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostLoad
    public void loadSchedule() {
        if (weeklyScheduleJson != null && !weeklyScheduleJson.isEmpty() && !weeklyScheduleJson.equals("null")) {
            try {
                this.weeklySchedule = objectMapper.readValue(weeklyScheduleJson, WeeklySchedule.class);
            } catch (JsonProcessingException e) {
                System.err.println("Error deserializing schedule: " + e.getMessage());
                this.weeklySchedule = null;
            }
        } else {
            this.weeklySchedule = null;
        }
    }

    @PrePersist
    @PreUpdate
    public void saveSchedule() {
        try {
            if (weeklySchedule != null) {
                this.weeklyScheduleJson = objectMapper.writeValueAsString(weeklySchedule);
                System.out.println("Serialized schedule to JSON: " + this.weeklyScheduleJson);
            } else {
                this.weeklyScheduleJson = null;
                System.out.println("Schedule is null, setting JSON to null");
            }
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing schedule: " + e.getMessage());
            this.weeklyScheduleJson = null;
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(BigDecimal ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public LocalDate getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDate joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getSubjectLabel() {
        return subjectLabel;
    }

    public void setSubjectLabel(String subjectLabel) {
        this.subjectLabel = subjectLabel;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public TeachingMethod getTeachingMethod() {
        return teachingMethod;
    }

    public void setTeachingMethod(TeachingMethod teachingMethod) {
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

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public VerificationTier getVerificationTier() {
        return verificationTier;
    }

    public void setVerificationTier(VerificationTier verificationTier) {
        this.verificationTier = verificationTier;
    }

    public WeeklySchedule getWeeklySchedule() {
        return weeklySchedule;
    }

    public void setWeeklySchedule(WeeklySchedule weeklySchedule) {
        this.weeklySchedule = weeklySchedule;
        // IMPORTANT: Manually trigger serialization
        saveSchedule();
    }

    public String getWeeklyScheduleJson() {
        return weeklyScheduleJson;
    }

    public void setWeeklyScheduleJson(String weeklyScheduleJson) {
        this.weeklyScheduleJson = weeklyScheduleJson;
    }
}