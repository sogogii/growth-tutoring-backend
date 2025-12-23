package com.growthtutoring.backend.feedback;

import java.util.List;

public class FeedbackRequest {
    private String name;
    private String email;
    private String comment;
    private String recaptchaToken;
    private List<FileAttachment> attachments;

    // Constructors
    public FeedbackRequest() {}

    public FeedbackRequest(String name, String email, String comment, String recaptchaToken, List<FileAttachment> attachments) {
        this.name = name;
        this.email = email;
        this.comment = comment;
        this.recaptchaToken = recaptchaToken;
        this.attachments = attachments;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRecaptchaToken() {
        return recaptchaToken;
    }

    public void setRecaptchaToken(String recaptchaToken) {
        this.recaptchaToken = recaptchaToken;
    }

    public List<FileAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileAttachment> attachments) {
        this.attachments = attachments;
    }
}