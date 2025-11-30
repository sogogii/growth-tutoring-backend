package com.growthtutoring.backend.chat.dto;

import java.time.Instant;

public class ConversationSummaryDto {

    private Long id;
    private Long otherUserId;
    private String otherName;

    // Latest message info
    private String lastMessageContent;
    private Instant lastMessageCreatedAt;

    // Unread messages in this conversation for the current viewer
    private long unreadCount;

    public ConversationSummaryDto() {
    }

    public ConversationSummaryDto(Long id, Long otherUserId, String otherName) {
        this.id = id;
        this.otherUserId = otherUserId;
        this.otherName = otherName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(Long otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public Instant getLastMessageCreatedAt() {
        return lastMessageCreatedAt;
    }

    public void setLastMessageCreatedAt(Instant lastMessageCreatedAt) {
        this.lastMessageCreatedAt = lastMessageCreatedAt;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
