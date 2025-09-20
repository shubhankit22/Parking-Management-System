package com.demo.parkinglot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Ticket {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Vehicle vehicle;

    @OneToOne
    private ParkingSlot slot;

    @ManyToOne
    private EntryGate entryGate;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    private boolean active;

    // Constructors, Getters, Setters
    public Ticket() {}

    public Ticket(Vehicle vehicle, ParkingSlot slot, EntryGate entryGate, LocalDateTime entryTime, boolean active) {
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryGate = entryGate;
        this.entryTime = entryTime;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public ParkingSlot getSlot() { return slot; }
    public void setSlot(ParkingSlot slot) { this.slot = slot; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public EntryGate getEntryGate() { return entryGate; }
    public void setEntryGate(EntryGate entryGate) { this.entryGate = entryGate; }
}
