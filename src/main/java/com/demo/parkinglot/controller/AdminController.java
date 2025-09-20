package com.demo.parkinglot.controller;

import com.demo.parkinglot.dto.*;
import com.demo.parkinglot.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * Update pricing rules for vehicle types
     */
    @PutMapping("/pricing-rules")
    public ResponseEntity<AdminResponse> updatePricingRules(@Valid @RequestBody PricingRuleRequest request) {
        AdminResponse response = adminService.updatePricingRules(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current pricing rules
     */
    @GetMapping("/pricing-rules")
    public ResponseEntity<AdminResponse> getPricingRules() {
        AdminResponse response = adminService.getPricingRules();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Add new parking slot
     */
    @PostMapping("/slots")
    public ResponseEntity<AdminResponse> addParkingSlot(@Valid @RequestBody SlotManagementRequest request) {
        AdminResponse response = adminService.addParkingSlot(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Remove parking slot
     */
    @DeleteMapping("/slots/{slotId}")
    public ResponseEntity<AdminResponse> removeParkingSlot(@PathVariable Long slotId) {
        AdminResponse response = adminService.removeParkingSlot(slotId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update parking slot
     */
    @PutMapping("/slots/{slotId}")
    public ResponseEntity<AdminResponse> updateParkingSlot(@PathVariable Long slotId, 
                                                          @Valid @RequestBody SlotManagementRequest request) {
        AdminResponse response = adminService.updateParkingSlot(slotId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get parking lot management overview
     */
    @GetMapping("/parking-lots/{parkingLotId}/overview")
    public ResponseEntity<AdminResponse> getParkingLotOverview(@PathVariable Long parkingLotId) {
        AdminResponse response = adminService.getParkingLotOverview(parkingLotId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Bulk add parking slots
     */
    @PostMapping("/slots/bulk")
    public ResponseEntity<AdminResponse> addBulkParkingSlots(@Valid @RequestBody List<SlotManagementRequest> requests) {
        AdminResponse response = adminService.addBulkParkingSlots(requests);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get slot management statistics
     */
    @GetMapping("/slots/statistics")
    public ResponseEntity<AdminResponse> getSlotStatistics() {
        AdminResponse response = adminService.getSlotStatistics();
        return ResponseEntity.ok(response);
    }
}
