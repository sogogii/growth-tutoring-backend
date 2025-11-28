package com.growthtutoring.backend.tutor;

import java.math.BigDecimal;

public class TutorReviewRequest {

    private Long userId;           // current logged-in user (student)
    private BigDecimal rating;     // 1.0 ~ 5.0
    private String comment;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
