package com.growthtutoring.backend.feedback;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDTO {
    private Long id;
    private String name;
    private String email;
    private String comment;
    private FeedbackStatus status;
    private Boolean isRead;
    private Instant createdAt;
    private Instant updatedAt;
    private List<FeedbackAttachmentDTO> attachments = new ArrayList<>();
    private String adminNotes;

    // Constructors
    public FeedbackDTO() {}

    public FeedbackDTO(Feedback feedback) {
        this.id = feedback.getId();
        this.name = feedback.getName();
        this.email = feedback.getEmail();
        this.comment = feedback.getComment();
        this.status = feedback.getStatus();
        this.isRead = feedback.getIsRead();
        this.createdAt = feedback.getCreatedAt();
        this.updatedAt = feedback.getUpdatedAt();
        this.adminNotes = feedback.getAdminNotes();

        if (feedback.getAttachments() != null) {
            for (FeedbackAttachmentEntity attachment : feedback.getAttachments()) {
                this.attachments.add(new FeedbackAttachmentDTO(attachment));
            }
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public FeedbackStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<FeedbackAttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FeedbackAttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
}