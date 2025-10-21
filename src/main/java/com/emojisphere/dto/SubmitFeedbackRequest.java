package com.emojisphere.dto;

import jakarta.validation.constraints.NotBlank;

public class SubmitFeedbackRequest {

    private String type;

    @NotBlank
    private String subject;

    @NotBlank
    private String message;

    public SubmitFeedbackRequest() {}

    public SubmitFeedbackRequest(String type, String subject, String message) {
        this.type = type;
        this.subject = subject;
        this.message = message;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
