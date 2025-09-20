package com.demo.parkinglot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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

    private String status; // PAID, FAILED

    // Constructors, Getters, Setters
    public Payment() {}

    public Payment(Ticket ticket, double amount, LocalDateTime paidAt, String status) {
        this.ticket = ticket;
        this.amount = amount;
        this.paidAt = paidAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
