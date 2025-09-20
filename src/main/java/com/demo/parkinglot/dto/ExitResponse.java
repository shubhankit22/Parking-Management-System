package com.demo.parkinglot.dto;

import com.demo.parkinglot.entity.Payment;
import com.demo.parkinglot.entity.Receipt;

public class ExitResponse {
    
    private Payment payment;
    private Receipt receipt;
    private String message;
    
    // Constructors
    public ExitResponse() {}
    
    public ExitResponse(Payment payment, Receipt receipt, String message) {
        this.payment = payment;
        this.receipt = receipt;
        this.message = message;
    }
    
    // Getters and Setters
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    
    public Receipt getReceipt() { return receipt; }
    public void setReceipt(Receipt receipt) { this.receipt = receipt; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
