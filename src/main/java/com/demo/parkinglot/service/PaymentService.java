package com.demo.parkinglot.service;

import com.demo.parkinglot.dto.ExitResponse;
import com.demo.parkinglot.entity.*;
import com.demo.parkinglot.repository.*;
import com.demo.parkinglot.util.ParkingUtility;
import com.demo.parkinglot.enums.PaymentStatus;
import com.demo.parkinglot.enums.VehicleType;
import com.demo.parkinglot.config.ParkingChargesConfig;
import com.demo.parkinglot.constants.ParkingConstants;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private ReceiptRepository receiptRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private ParkingSlotRepository slotRepository;
    
    @Autowired
    private ParkingChargesConfig parkingChargesConfig;
    
    /**
     * Process payment atomically - slot is only freed after successful payment
     */
    @Transactional
    public ExitResponse processPayment(Long ticketId, double amount) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException(ParkingConstants.INVALID_TICKET_ID));

        if (!ticket.isActive()) {
            throw new IllegalStateException(ParkingConstants.TICKET_INACTIVE);
        }

        LocalDateTime exitTime = LocalDateTime.now();

        // Calculate parking charges
        long durationInMinutes = ParkingUtility.calculateDurationInMinutes(ticket.getEntryTime(), exitTime);
        VehicleType vehicleType = ticket.getVehicle().getType();
        double hourlyRate = parkingChargesConfig.getHourlyRate(vehicleType);
        double calculatedAmount = ParkingUtility.calculateParkingCharge(durationInMinutes, hourlyRate);
        
        // Validate payment amount
        if (!ParkingUtility.isPaymentAmountValid(amount, calculatedAmount)) {
            // Create failed payment record
            Payment failedPayment = new Payment(ticket, amount, exitTime, PaymentStatus.FAILED);
            failedPayment.setFailureReason("Payment amount mismatch. Expected: " + calculatedAmount + ", Received: " + amount);
            paymentRepository.save(failedPayment);
            
            throw new IllegalArgumentException(ParkingConstants.PAYMENT_AMOUNT_MISMATCH + 
                " Expected: " + calculatedAmount + ", Received: " + amount);
        }

        try {
            // Simulate payment processing (in real implementation, this would call payment gateway)
            boolean paymentSuccessful = processPaymentWithGateway(amount, ticket);
            
            if (!paymentSuccessful) {
                // Payment failed - create failed payment record and keep slot occupied
                Payment failedPayment = new Payment(ticket, amount, exitTime, PaymentStatus.FAILED);
                failedPayment.setFailureReason("Payment gateway processing failed");
                paymentRepository.save(failedPayment);
                
                throw new IllegalStateException("Payment processing failed. Slot remains occupied.");
            }
            
            // Payment successful - create payment record
            Payment payment = new Payment(ticket, amount, exitTime, PaymentStatus.PAID);
            payment = paymentRepository.save(payment);

            // Update ticket status
            ticket.setExitTime(exitTime);
            ticket.setActive(false);
            ticketRepository.save(ticket);

            // Generate receipt
            String receiptNumber = ParkingUtility.generateReceiptNumber();
            Receipt receipt = new Receipt(ticket, calculatedAmount, hourlyRate, durationInMinutes, receiptNumber);
            receipt = receiptRepository.save(receipt);

            // ONLY NOW free the parking slot - this is the atomic operation
            ParkingSlot slot = ticket.getSlot();
            int freedRows = slotRepository.freeSlot(slot.getId());
            if (freedRows != 1) {
                // This should not happen, but if it does, we have a serious issue
                throw new IllegalStateException("Failed to free parking slot after successful payment");
            }

            return new ExitResponse(payment, receipt, ParkingConstants.VEHICLE_EXIT_SUCCESS);
            
        } catch (Exception e) {
            // Any exception during payment processing means slot remains occupied
            Payment failedPayment = new Payment(ticket, amount, exitTime, PaymentStatus.FAILED);
            failedPayment.setFailureReason("Payment processing exception: " + e.getMessage());
            paymentRepository.save(failedPayment);
            
            throw new IllegalStateException("Payment processing failed. Slot remains occupied. Error: " + e.getMessage());
        }
    }
    
    /**
     * Simulate payment gateway processing
     * In a real implementation, this would integrate with actual payment gateways
     */
    private boolean processPaymentWithGateway(double amount, Ticket ticket) {
        // Simulate payment gateway processing
        // In real implementation, this would:
        // 1. Call payment gateway API
        // 2. Handle various payment methods (credit card, digital wallet, etc.)
        // 3. Process payment authorization
        // 4. Handle payment failures and retries
        
        // For simulation, we'll randomly fail 5% of payments
        if (Math.random() < 0.05) {
            return false; // Simulate payment failure
        }
        
        // Simulate processing delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        return true; // Payment successful
    }
    
    /**
     * Get payment history for a ticket
     */
    public Payment getPaymentByTicketId(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException(ParkingConstants.INVALID_TICKET_ID));
        
        return paymentRepository.findByTicket(ticket)
                .orElseThrow(() -> new IllegalArgumentException("No payment found for ticket"));
    }
    
    /**
     * Retry failed payment
     */
    @Transactional
    public ExitResponse retryPayment(Long ticketId, double amount) {
        // Check if there's a failed payment for this ticket
        Payment existingPayment = getPaymentByTicketId(ticketId);
        
        if (existingPayment.getStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Payment already successful for this ticket");
        }
        
        // Process new payment attempt
        return processPayment(ticketId, amount);
    }
}
