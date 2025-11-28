package com.growthtutoring.backend.matching.dto;

public class LinkStatusResponse {

    private String status;

    public LinkStatusResponse() {
    }

    public LinkStatusResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}