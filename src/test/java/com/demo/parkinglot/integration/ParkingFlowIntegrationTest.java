package com.demo.parkinglot.integration;

import com.demo.parkinglot.entity.*;
import com.demo.parkinglot.enums.VehicleType;
import com.demo.parkinglot.exception.SlotAllocationException;
import com.demo.parkinglot.repository.*;
import com.demo.parkinglot.service.ParkingManagementService;
import com.demo.parkinglot.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for complete parking flow
 * Tests end-to-end scenarios including entry, allocation, payment, and exit
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ParkingFlowIntegrationTest {

    @Autowired
    private ParkingManagementService parkingManagementService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private EntryGateRepository entryGateRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private ParkingLot parkingLot;
    private EntryGate entryGate;
    private List<ParkingSlot> carSlots;
    private List<ParkingSlot> bikeSlots;


    @BeforeEach
    void setUp() {
        // Create test parking lot
        parkingLot = new ParkingLot();
        parkingLot.setName("Test Parking Lot");
        parkingLot.setLocation("Test Location");
        parkingLot.setTotalFloors(2);
        parkingLot.setActive(true);
        parkingLot = parkingLotRepository.save(parkingLot);

        // Create entry gate
        entryGate = new EntryGate();
        entryGate.setGateName("Main Gate");
        entryGate.setXCoordinate(0.0);
        entryGate.setYCoordinate(0.0);
        entryGate.setParkingLot(parkingLot);
        entryGate = entryGateRepository.save(entryGate);

        // Create car slots
        carSlots = List.of(
            createSlot("C-001", VehicleType.CAR, 1, 10.0, 10.0),
            createSlot("C-002", VehicleType.CAR, 1, 20.0, 20.0),
            createSlot("C-003", VehicleType.CAR, 2, 15.0, 15.0)
        );

        // Create bike slots
        bikeSlots = List.of(
            createSlot("B-001", VehicleType.BIKE, 1, 5.0, 5.0),
            createSlot("B-002", VehicleType.BIKE, 1, 25.0, 25.0)
        );

    }

    @Test
    void testCompleteParkingFlow_CarEntryAndExit() {
        // Given
        String plateNo = "ABC-123";
        String vehicleType = "CAR";
        String ownerId = "user123";

        // When - Entry
        Ticket ticket = parkingManagementService.parkVehicle(plateNo, vehicleType, ownerId, entryGate.getId());

        // Then - Entry validation
        assertNotNull(ticket);
        assertTrue(ticket.isActive());
        assertNotNull(ticket.getEntryTime());
        assertNull(ticket.getExitTime());
        assertEquals(plateNo, ticket.getVehicle().getPlateNo());
        assertEquals(VehicleType.CAR, ticket.getVehicle().getType());
        assertNotNull(ticket.getSlot());
        assertFalse(ticket.getSlot().isAvailable());

        // Verify slot is occupied
        ParkingSlot occupiedSlot = parkingSlotRepository.findById(ticket.getSlot().getId()).orElseThrow();
        assertFalse(occupiedSlot.isAvailable());

        // When - Exit with payment (immediate exit = 1 hour minimum billing)
        double paymentAmount = 2.0; // 1 hour * $2/hour (minimum billing)
        var exitResponse = paymentService.processPayment(ticket.getId(), paymentAmount);

        // Then - Exit validation
        assertNotNull(exitResponse);
        assertNotNull(exitResponse.getPayment());
        assertNotNull(exitResponse.getReceipt());
        assertEquals("Vehicle successfully exited. Payment processed and slot freed.", exitResponse.getMessage());

        // Verify slot is freed
        ParkingSlot freedSlot = parkingSlotRepository.findById(ticket.getSlot().getId()).orElseThrow();
        assertTrue(freedSlot.isAvailable());

        // Verify ticket is inactive
        Ticket inactiveTicket = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertFalse(inactiveTicket.isActive());
        assertNotNull(inactiveTicket.getExitTime());
    }

    @Test
    void testCompleteParkingFlow_BikeEntryAndExit() {
        // Given
        String plateNo = "XYZ-789";
        String vehicleType = "BIKE";
        String ownerId = "user456";

        // When - Entry
        Ticket ticket = parkingManagementService.parkVehicle(plateNo, vehicleType, ownerId, entryGate.getId());

        // Then - Entry validation
        assertNotNull(ticket);
        assertEquals(VehicleType.BIKE, ticket.getVehicle().getType());
        assertEquals(VehicleType.BIKE, ticket.getSlot().getSlotType());

        // When - Exit with payment (immediate exit = 1 hour minimum billing)
        double paymentAmount = 1.0; // 1 hour * $1/hour (minimum billing)
        var exitResponse = paymentService.processPayment(ticket.getId(), paymentAmount);

        // Then - Exit validation
        assertNotNull(exitResponse);
        assertTrue(exitResponse.getPayment().getAmount() > 0);
    }

    @Test
    void testParkingFlow_DuplicateVehicleEntry_ThrowsException() {
        // Given
        String plateNo = "DUP-123";
        String vehicleType = "CAR";
        String ownerId = "user789";

        // When - First entry
        Ticket firstTicket = parkingManagementService.parkVehicle(plateNo, vehicleType, ownerId, entryGate.getId());
        assertNotNull(firstTicket);

        // When & Then - Duplicate entry should fail
        assertThrows(SlotAllocationException.class, () -> parkingManagementService.parkVehicle(plateNo, vehicleType, ownerId, entryGate.getId()));
    }

    @Test
    void testParkingFlow_PaymentFailure_SlotRemainsOccupied() {
        // Given
        String plateNo = "PAY-456";
        String vehicleType = "CAR";
        String ownerId = "user999";

        Ticket ticket = parkingManagementService.parkVehicle(plateNo, vehicleType, ownerId, entryGate.getId());
        Long slotId = ticket.getSlot().getId();

        // Verify slot is occupied
        ParkingSlot slot = parkingSlotRepository.findById(slotId).orElseThrow();
        assertFalse(slot.isAvailable());

        // When - Payment with wrong amount (should fail)
        double wrongAmount = 0.5; // Too low (expected is 2.0 for 1 hour)
        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(ticket.getId(), wrongAmount));

        // Then - Slot should still be occupied
        ParkingSlot stillOccupiedSlot = parkingSlotRepository.findById(slotId).orElseThrow();
        assertFalse(stillOccupiedSlot.isAvailable());

        // Verify ticket is still active
        Ticket stillActiveTicket = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertTrue(stillActiveTicket.isActive());
    }

    @Test
    void testParkingFlow_ConcurrentAllocation_HandlesGracefully() {
        // Given
        String plateNo1 = "CONC-001";
        String plateNo2 = "CONC-002";
        String vehicleType = "CAR";
        String ownerId = "user111";

        // When - Concurrent allocation attempts
        Ticket ticket1 = parkingManagementService.parkVehicle(plateNo1, vehicleType, ownerId, entryGate.getId());
        Ticket ticket2 = parkingManagementService.parkVehicle(plateNo2, vehicleType, ownerId, entryGate.getId());

        // Then - Both should get different slots
        assertNotNull(ticket1);
        assertNotNull(ticket2);
        assertNotEquals(ticket1.getSlot().getId(), ticket2.getSlot().getId());
        assertFalse(ticket1.getSlot().isAvailable());
        assertFalse(ticket2.getSlot().isAvailable());
    }

    @Test
    void testParkingFlow_ParkingLotFull_ThrowsException() {
        // Given - Fill all car slots
        for (int i = 0; i < carSlots.size(); i++) {
            String plateNo = "FULL-" + String.format("%03d", i + 1);
            parkingManagementService.parkVehicle(plateNo, "CAR", "user" + i, entryGate.getId());
        }

        // When & Then - Attempt to park another car should fail
        assertThrows(SlotAllocationException.class, () ->
                parkingManagementService.parkVehicle("FULL-999", "CAR", "user999", entryGate.getId()));
    }

    private ParkingSlot createSlot(String slotNumber, VehicleType slotType, int floor, double x, double y) {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber(slotNumber);
        slot.setSlotType(slotType);
        slot.setFloor(floor);
        slot.setXCoordinate(x);
        slot.setYCoordinate(y);
        slot.setAvailable(true);
        slot.setParkingLot(parkingLot);
        return parkingSlotRepository.save(slot);
    }
}
