package com.demo.parkinglot.service;

import com.demo.parkinglot.entity.EntryGate;
import com.demo.parkinglot.entity.ParkingLot;
import com.demo.parkinglot.entity.ParkingSlot;
import com.demo.parkinglot.enums.AllocationStrategyType;
import com.demo.parkinglot.enums.VehicleType;
import com.demo.parkinglot.exception.SlotAllocationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SlotAllocationManager
 * Tests various scenarios including edge cases and error conditions
 */
@ExtendWith(MockitoExtension.class)
class SlotAllocationManagerTest {

    @Mock
    private SlotAllocationService slotAllocationService;

    @InjectMocks
    private SlotAllocationManager slotAllocationManager;

    private EntryGate entryGate;
    private List<ParkingSlot> availableSlots;
    private ParkingSlot nearestSlot;
    private ParkingLot parkingLot;

    @BeforeEach
    void setUp() {
        // Setup test data
        parkingLot = new ParkingLot();
        parkingLot.setId(1L);
        parkingLot.setName("Test Parking Lot");

        entryGate = new EntryGate();
        entryGate.setId(1L);
        entryGate.setXCoordinate(0.0);
        entryGate.setYCoordinate(0.0);

        nearestSlot = new ParkingSlot();
        nearestSlot.setId(1L);
        nearestSlot.setSlotNumber("A-001");
        nearestSlot.setFloor(1);
        nearestSlot.setXCoordinate(10.0);
        nearestSlot.setYCoordinate(10.0);
        nearestSlot.setAvailable(true);
        nearestSlot.setSlotType(VehicleType.CAR);
        nearestSlot.setParkingLot(parkingLot);

        ParkingSlot farSlot = new ParkingSlot();
        farSlot.setId(2L);
        farSlot.setSlotNumber("A-002");
        farSlot.setFloor(1);
        farSlot.setXCoordinate(50.0);
        farSlot.setYCoordinate(50.0);
        farSlot.setAvailable(true);
        farSlot.setSlotType(VehicleType.CAR);
        farSlot.setParkingLot(parkingLot);

        availableSlots = Arrays.asList(nearestSlot, farSlot);
    }

    @Test
    void testAllocateOptimalSlot_Success() {
        // Given
        when(slotAllocationService.allocateSlot(any(), any())).thenReturn(nearestSlot);

        // When
        ParkingSlot result = slotAllocationManager.allocateOptimalSlot(availableSlots, entryGate);

        // Then
        assertNotNull(result);
        assertEquals(nearestSlot.getId(), result.getId());
        verify(slotAllocationService).allocateSlot(availableSlots, entryGate);
    }

    @Test
    void testAllocateOptimalSlot_WithNullSlots_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> slotAllocationManager.allocateOptimalSlot(null, entryGate)
        );
        assertEquals("Available slots list cannot be null or empty", exception.getMessage());
        verifyNoInteractions(slotAllocationService);
    }

    @Test
    void testAllocateOptimalSlot_WithEmptySlots_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> slotAllocationManager.allocateOptimalSlot(Collections.emptyList(), entryGate)
        );
        assertEquals("Available slots list cannot be null or empty", exception.getMessage());
        verifyNoInteractions(slotAllocationService);
    }

    @Test
    void testAllocateOptimalSlot_WithNullEntryGate_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> slotAllocationManager.allocateOptimalSlot(availableSlots, null)
        );
        assertEquals("Entry gate cannot be null", exception.getMessage());
        verifyNoInteractions(slotAllocationService);
    }

    @Test
    void testAllocateOptimalSlot_WithUnavailableSlots_ThrowsException() {
        // Given
        nearestSlot.setAvailable(false);
        when(slotAllocationService.allocateSlot(any(), any())).thenReturn(nearestSlot);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> slotAllocationManager.allocateOptimalSlot(availableSlots, entryGate)
        );
        assertEquals("Found 1 unavailable slots in the provided list", exception.getMessage());
        verifyNoInteractions(slotAllocationService);
    }

    @Test
    void testAllocateSlotWithStrategy_Success() {
        // Given
        AllocationStrategyType strategy = AllocationStrategyType.NEAREST_SLOT;
        when(slotAllocationService.allocateSlot(any(), any(), eq(strategy))).thenReturn(nearestSlot);

        // When
        ParkingSlot result = slotAllocationManager.allocateSlotWithStrategy(availableSlots, entryGate, strategy);

        // Then
        assertNotNull(result);
        assertEquals(nearestSlot.getId(), result.getId());
        verify(slotAllocationService).allocateSlot(availableSlots, entryGate, strategy);
    }

    @Test
    void testAllocateSlotWithStrategy_ServiceThrowsException_PropagatesException() {
        // Given
        AllocationStrategyType strategy = AllocationStrategyType.NEAREST_SLOT;
        when(slotAllocationService.allocateSlot(any(), any(), eq(strategy)))
            .thenThrow(new SlotAllocationException("Allocation failed"));

        // When & Then
        SlotAllocationException exception = assertThrows(
            SlotAllocationException.class,
            () -> slotAllocationManager.allocateSlotWithStrategy(availableSlots, entryGate, strategy)
        );
        assertEquals("Allocation failed", exception.getMessage());
    }

    @Test
    void testGetCurrentStrategy() {
        // Given
        AllocationStrategyType expectedStrategy = AllocationStrategyType.LEVEL_WISE;
        when(slotAllocationService.getCurrentStrategy()).thenReturn(expectedStrategy);

        // When
        AllocationStrategyType result = slotAllocationManager.getCurrentStrategy();

        // Then
        assertEquals(expectedStrategy, result);
        verify(slotAllocationService).getCurrentStrategy();
    }

    @Test
    void testIsStrategyValid_ValidStrategy_ReturnsTrue() {
        // Given
        String validStrategy = "NEAREST_SLOT";
        when(slotAllocationService.isValidStrategy(validStrategy)).thenReturn(true);

        // When
        boolean result = slotAllocationManager.isStrategyValid(validStrategy);

        // Then
        assertTrue(result);
        verify(slotAllocationService).isValidStrategy(validStrategy);
    }

    @Test
    void testIsStrategyValid_InvalidStrategy_ReturnsFalse() {
        // Given
        String invalidStrategy = "INVALID_STRATEGY";
        when(slotAllocationService.isValidStrategy(invalidStrategy)).thenReturn(false);

        // When
        boolean result = slotAllocationManager.isStrategyValid(invalidStrategy);

        // Then
        assertFalse(result);
        verify(slotAllocationService).isValidStrategy(invalidStrategy);
    }

    @Test
    void testGetAvailableStrategies() {
        // Given
        AllocationStrategyType[] expectedStrategies = AllocationStrategyType.getAllStrategies();
        when(slotAllocationService.getAvailableStrategies()).thenReturn(expectedStrategies);

        // When
        AllocationStrategyType[] result = slotAllocationManager.getAvailableStrategies();

        // Then
        assertArrayEquals(expectedStrategies, result);
        verify(slotAllocationService).getAvailableStrategies();
    }

    @Test
    void testValidateAllocationParameters_AllValid_NoException() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> {
            slotAllocationManager.allocateOptimalSlot(availableSlots, entryGate);
        });
    }

    @Test
    void testValidateAllocationParameters_MixedAvailability_ThrowsException() {
        // Given
        ParkingSlot unavailableSlot = new ParkingSlot();
        unavailableSlot.setId(3L);
        unavailableSlot.setAvailable(false);
        unavailableSlot.setSlotType(VehicleType.CAR);
        unavailableSlot.setParkingLot(parkingLot);

        List<ParkingSlot> mixedSlots = Arrays.asList(nearestSlot, unavailableSlot);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> slotAllocationManager.allocateOptimalSlot(mixedSlots, entryGate)
        );
        assertEquals("Found 1 unavailable slots in the provided list", exception.getMessage());
    }
}
