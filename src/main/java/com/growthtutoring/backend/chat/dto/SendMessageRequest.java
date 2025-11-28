package com.growthtutoring.backend.chat.dto;

public class SendMessageRequest {

    private String content;

    public SendMessageRequest() {
    }

    public SendMessageRequest(String content) {
        this.content = content;
    }

    // --------- Getters & Setters ----------

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}