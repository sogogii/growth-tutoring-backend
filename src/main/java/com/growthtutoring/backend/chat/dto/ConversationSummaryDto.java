package com.growthtutoring.backend.chat.dto;

public class ConversationSummaryDto {

    private Long id;
    private Long otherUserId;
    private String otherName;

    // NEW
    private String otherFirstName;
    private String otherLastName;
    private String otherAvatarUrl;

    public ConversationSummaryDto() {
    }

    // keep old 3-arg constructor
    public ConversationSummaryDto(Long id, Long otherUserId, String otherName) {
        this(id, otherUserId, otherName, null, null, null);
    }

    public ConversationSummaryDto(
            Long id,
            Long otherUserId,
            String otherName,
            String otherFirstName,
            String otherLastName,
            String otherAvatarUrl
    ) {
        this.id = id;
        this.otherUserId = otherUserId;
        this.otherName = otherName;
        this.otherFirstName = otherFirstName;
        this.otherLastName = otherLastName;
        this.otherAvatarUrl = otherAvatarUrl;
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

    public String getOtherFirstName() {
        return otherFirstName;
    }

    public void setOtherFirstName(String otherFirstName) {
        this.otherFirstName = otherFirstName;
    }

    public String getOtherLastName() {
        return otherLastName;
    }

    public void setOtherLastName(String otherLastName) {
        this.otherLastName = otherLastName;
    }

    public String getOtherAvatarUrl() {
        return otherAvatarUrl;
    }

    public void setOtherAvatarUrl(String otherAvatarUrl) {
        this.otherAvatarUrl = otherAvatarUrl;
    }
}
