package com.demo.parkinglot.entity;

import com.demo.parkinglot.constants.ParkingConstants;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class Receipt {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @OneToOne
    private Ticket ticket;
    
    private double totalAmount;
    private double hourlyRate;
    private long durationInMinutes;
    private LocalDateTime generatedAt;
    private String receiptNumber;
    
    // Constructors
    public Receipt() {}
    
    public Receipt(Ticket ticket, double totalAmount, double hourlyRate, long durationInMinutes, String receiptNumber) {
        this.ticket = ticket;
        this.totalAmount = totalAmount;
        this.hourlyRate = hourlyRate;
        this.durationInMinutes = durationInMinutes;
        this.generatedAt = LocalDateTime.now();
        this.receiptNumber = receiptNumber;
    }
    
    public Receipt(Ticket ticket, double totalAmount, double hourlyRate, long durationInMinutes) {
        this(ticket, totalAmount, hourlyRate, durationInMinutes, ParkingConstants.RECEIPT_PREFIX + "-" + System.currentTimeMillis());
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    
    public long getDurationInMinutes() { return durationInMinutes; }
    public void setDurationInMinutes(long durationInMinutes) { this.durationInMinutes = durationInMinutes; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
}
