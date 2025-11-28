package com.growthtutoring.backend.tutor;

import java.math.BigDecimal;

public class UpdateTutorProfileRequest {

    private String subjectLabel;   // "K-12 Math, Physics"
    private BigDecimal hourlyRate;
    private String teachingMethod; // "ONLINE" / "IN_PERSON" / "HYBRID"
    private String bio;

    public String getSubjectLabel() { return subjectLabel; }
    public void setSubjectLabel(String subjectLabel) { this.subjectLabel = subjectLabel; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getTeachingMethod() { return teachingMethod; }
    public void setTeachingMethod(String teachingMethod) { this.teachingMethod = teachingMethod; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
