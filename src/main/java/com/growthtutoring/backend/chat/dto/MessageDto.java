package com.growthtutoring.backend.chat.dto;

import java.time.Instant;

public class MessageDto {

    private Long id;
    private Long senderUserId;
    private String content;
    private Instant createdAt;

    // NEW FIELDS
    private String senderFirstName;
    private String senderLastName;
    private String senderAvatarUrl;

    public MessageDto() {
    }

    // old 4-arg constructor (kept for compatibility if anything else uses it)
    public MessageDto(Long id, Long senderUserId, String content, Instant createdAt) {
        this(id, senderUserId, content, createdAt, null, null, null);
    }

    // NEW full constructor
    public MessageDto(
            Long id,
            Long senderUserId,
            String content,
            Instant createdAt,
            String senderFirstName,
            String senderLastName,
            String senderAvatarUrl
    ) {
        this.id = id;
        this.senderUserId = senderUserId;
        this.content = content;
        this.createdAt = createdAt;
        this.senderFirstName = senderFirstName;
        this.senderLastName = senderLastName;
        this.senderAvatarUrl = senderAvatarUrl;
    }

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

    public String getSenderFirstName() {
        return senderFirstName;
    }

    public void setSenderFirstName(String senderFirstName) {
        this.senderFirstName = senderFirstName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }

    public void setSenderLastName(String senderLastName) {
        this.senderLastName = senderLastName;
    }

    public String getSenderAvatarUrl() {
        return senderAvatarUrl;
    }

    public void setSenderAvatarUrl(String senderAvatarUrl) {
        this.senderAvatarUrl = senderAvatarUrl;
    }
}
