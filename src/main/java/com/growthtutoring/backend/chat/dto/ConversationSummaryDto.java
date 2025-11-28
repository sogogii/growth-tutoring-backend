package com.growthtutoring.backend.chat.dto;

public class ConversationSummaryDto {

    private Long id;
    private Long otherUserId;
    private String otherName;

    public ConversationSummaryDto() {
    }

    public ConversationSummaryDto(Long id, Long otherUserId, String otherName) {
        this.id = id;
        this.otherUserId = otherUserId;
        this.otherName = otherName;
    }

    // --------- Getters & Setters ----------

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
}