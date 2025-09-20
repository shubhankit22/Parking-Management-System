package com.demo.parkinglot.entity;

import com.demo.parkinglot.enums.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Ticket ticket;

    private double amount;

    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    private String failureReason;

    // Constructors, Getters, Setters
    public Payment() {}

    public Payment(Ticket ticket, double amount, LocalDateTime paidAt, PaymentStatus status) {
        this.ticket = ticket;
        this.amount = amount;
        this.paidAt = paidAt;
        this.status = status;
    }
    
    public Payment(Ticket ticket, double amount, LocalDateTime paidAt, String statusString) {
        this.ticket = ticket;
        this.amount = amount;
        this.paidAt = paidAt;
        this.status = PaymentStatus.fromString(statusString);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    
    public void setStatus(String statusString) { 
        this.status = PaymentStatus.fromString(statusString); 
    }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}
