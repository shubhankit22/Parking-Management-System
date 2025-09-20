package com.demo.parkinglot.service;

import com.demo.parkinglot.dto.ExitResponse;
import com.demo.parkinglot.entity.*;
import com.demo.parkinglot.repository.*;
import com.demo.parkinglot.util.ParkingUtility;
import com.demo.parkinglot.enums.VehicleType;
import com.demo.parkinglot.config.ParkingChargesConfig;
import com.demo.parkinglot.constants.ParkingConstants;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    
    @Autowired
    private ParkingChargesConfig parkingChargesConfig;
    
    @Autowired
    private ParkingManagementService parkingManagementService;

    @Transactional
    public Ticket parkVehicle(String plateNo, String type, String ownerId, Long entryGateId) {
        return parkingManagementService.parkVehicle(plateNo, type, ownerId, entryGateId);
    }

    @Transactional
    public ExitResponse unparkVehicle(Long ticketId, double amount) {
        return parkingManagementService.unparkVehicle(ticketId, amount);
    }
    
    /**
     * Get parking lot status with floor-wise availability
     */
    public Map<String, Object> getParkingLotStatus(Long parkingLotId) {
        return parkingManagementService.getParkingLotStatus(parkingLotId);
    }
    
    /**
     * Retry failed payment
     */
    @Transactional
    public ExitResponse retryPayment(Long ticketId, double amount) {
        return parkingManagementService.retryPayment(ticketId, amount);
    }
}
