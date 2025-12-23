package com.growthtutoring.backend.feedback;

public class FeedbackAttachmentDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;

    // Constructors
    public FeedbackAttachmentDTO() {}

    public FeedbackAttachmentDTO(FeedbackAttachmentEntity entity) {
        this.id = entity.getId();
        this.fileName = entity.getFileName();
        this.fileType = entity.getFileType();
        this.fileSize = entity.getFileSize();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}