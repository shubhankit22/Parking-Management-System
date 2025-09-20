package com.demo.parkinglot.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class AdminResponse {
    
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, Object> data;
    
    // Constructors
    public AdminResponse() {}
    
    public AdminResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    public AdminResponse(boolean success, String message, Map<String, Object> data) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
