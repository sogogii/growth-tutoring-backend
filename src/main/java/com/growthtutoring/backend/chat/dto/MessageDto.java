package com.growthtutoring.backend.chat.dto;

import java.time.Instant;

public class MessageDto {

    private Long id;
    private Long senderUserId;
    private String content;
    private Instant createdAt;

    public MessageDto(Long id, Long senderUserId, String content, Instant createdAt) {
        this.id = id;
        this.senderUserId = senderUserId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public MessageDto() {
    }

    // --------- Getters & Setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}