package com.demo.parkinglot.service;

import com.demo.parkinglot.dto.ExitResponse;
import com.demo.parkinglot.entity.*;
import com.demo.parkinglot.repository.*;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {

    @Autowired
    private ParkingSlotRepository slotRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private EntryGateRepository entryGateRepository;
    
    @Autowired
    private ReceiptRepository receiptRepository;

    @Transactional
    public Ticket parkVehicle(String plateNo, String type, String ownerId, Long entryGateId) {
        Vehicle vehicle = vehicleRepository.findByPlateNo(plateNo)
                .orElseGet(() -> vehicleRepository.save(new Vehicle(plateNo, type, ownerId)));

        if (ticketRepository.findByVehicleAndActiveTrue(vehicle).isPresent()) {
            throw new IllegalStateException("Vehicle already parked");
        }

        EntryGate entryGate = entryGateRepository.findById(entryGateId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid entry gate ID"));

        List<ParkingSlot> availableSlots = slotRepository.findAllAvailableSlotsByType(type);
        if (availableSlots.isEmpty()) {
            throw new IllegalStateException("No available slot for type: " + type);
        }

        // Find the nearest slot to the entry gate
        ParkingSlot nearestSlot = findNearestSlot(availableSlots, entryGate);
        nearestSlot.setAvailable(false);
        slotRepository.save(nearestSlot);

        Ticket ticket = new Ticket(vehicle, nearestSlot, entryGate, LocalDateTime.now(), true);
        return ticketRepository.save(ticket);
    }
    
    private ParkingSlot findNearestSlot(List<ParkingSlot> slots, EntryGate entryGate) {
        return slots.stream()
                .min((slot1, slot2) -> {
                    double distance1 = calculateDistance(entryGate, slot1);
                    double distance2 = calculateDistance(entryGate, slot2);
                    return Double.compare(distance1, distance2);
                })
                .orElse(slots.get(0));
    }
    
    private double calculateDistance(EntryGate gate, ParkingSlot slot) {
        double deltaX = gate.getXCoordinate() - slot.getXCoordinate();
        double deltaY = gate.getYCoordinate() - slot.getYCoordinate();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    @Transactional
    public ExitResponse unparkVehicle(Long ticketId, double amount) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));

        if (!ticket.isActive()) {
            throw new IllegalStateException("Ticket is already inactive");
        }

        LocalDateTime exitTime = LocalDateTime.now();
        ticket.setExitTime(exitTime);
        ticket.setActive(false);
        ticketRepository.save(ticket);

        // Calculate parking charges
        long durationInMinutes = ChronoUnit.MINUTES.between(ticket.getEntryTime(), exitTime);
        double hourlyRate = getHourlyRate(ticket.getVehicle().getType());
        double calculatedAmount = calculateParkingCharge(durationInMinutes, hourlyRate);
        
        // Validate payment amount
        if (Math.abs(amount - calculatedAmount) > 0.01) {
            throw new IllegalArgumentException("Payment amount " + amount + " does not match calculated amount " + calculatedAmount);
        }

        // Create payment record
        Payment payment = new Payment(ticket, amount, exitTime, "PAID");
        payment = paymentRepository.save(payment);

        // Generate receipt
        String receiptNumber = generateReceiptNumber();
        Receipt receipt = new Receipt(ticket, calculatedAmount, hourlyRate, durationInMinutes, receiptNumber);
        receipt = receiptRepository.save(receipt);

        // Free the parking slot
        ParkingSlot slot = ticket.getSlot();
        slot.setAvailable(true);
        slotRepository.save(slot);

        return new ExitResponse(payment, receipt, "Vehicle successfully exited. Payment processed and slot freed.");
    }
    
    private double getHourlyRate(String vehicleType) {
        return switch (vehicleType.toUpperCase()) {
            case "CAR" -> 2.0;
            case "BIKE" -> 1.0;
            case "TRUCK" -> 5.0;
            default -> 2.0;
        };
    }
    
    private double calculateParkingCharge(long durationInMinutes, double hourlyRate) {
        // Round up to the next hour for billing
        long hours = (durationInMinutes + 59) / 60; // Round up
        return hours * hourlyRate;
    }
    
    private String generateReceiptNumber() {
        return "RCP-" + System.currentTimeMillis();
    }
}
