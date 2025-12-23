package com.growthtutoring.backend.feedback;

public class FileAttachment {
    private String name;
    private String type;
    private Long size;
    private String data; // Base64 encoded file data

    // Constructors
    public FileAttachment() {}

    public FileAttachment(String name, String type, Long size, String data) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.data = data;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}