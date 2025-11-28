package com.growthtutoring.backend.tutor;

import java.math.BigDecimal;
import java.time.Instant;

public class TutorReviewDto {

    private Long id;
    private BigDecimal rating;
    private String comment;
    private Instant createdAt;

    // basic student info so you can show "by Sungok"
    private Long studentUserId;
    private String studentFirstName;
    private String studentLastName;

    public TutorReviewDto(
            Long id,
            BigDecimal rating,
            String comment,
            Instant createdAt,
            Long studentUserId,
            String studentFirstName,
            String studentLastName
    ) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.studentUserId = studentUserId;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
    }

    // getters only (or add setters if you want)
    public Long getId() { return id; }
    public BigDecimal getRating() { return rating; }
    public String getComment() { return comment; }
    public Instant getCreatedAt() { return createdAt; }
    public Long getStudentUserId() { return studentUserId; }
    public String getStudentFirstName() { return studentFirstName; }
    public String getStudentLastName() { return studentLastName; }
}
