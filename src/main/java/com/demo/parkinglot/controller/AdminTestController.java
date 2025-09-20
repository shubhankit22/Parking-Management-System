package com.demo.parkinglot.controller;

import com.demo.parkinglot.dto.AdminResponse;
import com.demo.parkinglot.dto.PricingRuleRequest;
import com.demo.parkinglot.dto.SlotManagementRequest;
import com.demo.parkinglot.enums.VehicleType;
import com.demo.parkinglot.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/admin/test")
public class AdminTestController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * Test pricing rules update
     */
    @PostMapping("/pricing-test")
    public ResponseEntity<AdminResponse> testPricingUpdate() {
        // Test updating bike pricing
        PricingRuleRequest bikeRequest = new PricingRuleRequest(VehicleType.BIKE, 1.5, "Updated bike rate", true);
        return ResponseEntity.ok(adminService.updatePricingRules(bikeRequest));
    }
    
    /**
     * Test slot addition
     */
    @PostMapping("/slot-test")
    public ResponseEntity<AdminResponse> testSlotAddition() {
        // Test adding a new car slot
        SlotManagementRequest slotRequest = new SlotManagementRequest(
            1L, // parkingLotId
            1,  // floor
            VehicleType.CAR, // slotType
            "A-101", // slotNumber
            10.0, // xCoordinate
            20.0, // yCoordinate
            "ADD" // action
        );
        return ResponseEntity.ok(adminService.addParkingSlot(slotRequest));
    }
    
    /**
     * Test bulk slot addition
     */
    @PostMapping("/bulk-slot-test")
    public ResponseEntity<AdminResponse> testBulkSlotAddition() {
        List<SlotManagementRequest> requests = Arrays.asList(
            new SlotManagementRequest(1L, 1, VehicleType.CAR, "A-102", 15.0, 20.0, "ADD"),
            new SlotManagementRequest(1L, 1, VehicleType.BIKE, "B-101", 20.0, 20.0, "ADD"),
            new SlotManagementRequest(1L, 2, VehicleType.TRUCK, "T-201", 25.0, 20.0, "ADD")
        );
        return ResponseEntity.ok(adminService.addBulkParkingSlots(requests));
    }
    
    /**
     * Test statistics
     */
    @GetMapping("/statistics-test")
    public ResponseEntity<AdminResponse> testStatistics() {
        return ResponseEntity.ok(adminService.getSlotStatistics());
    }
}
