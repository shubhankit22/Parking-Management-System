package com.demo.parkinglot.controller;

import com.demo.parkinglot.dto.ParkingRequest;
import com.demo.parkinglot.dto.PaymentRequest;
import com.demo.parkinglot.dto.ExitResponse;
import com.demo.parkinglot.entity.*;
import com.demo.parkinglot.service.ParkingService;
import com.demo.parkinglot.repository.EntryGateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ParkingController {

    @Value("${spring.application.name:Parking Lot System}")
    private String appName;

    @Autowired
    private ParkingService parkingService;
    
    @Autowired
    private EntryGateRepository entryGateRepository;

    @PostMapping("/entry")
    public ResponseEntity<Ticket> parkVehicle(@RequestBody ParkingRequest request) {
        Ticket ticket = parkingService.parkVehicle(request.getPlateNo(), request.getVehicleType(), request.getOwnerId(), request.getEntryGateId());
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/exit/{ticketId}")
    public ResponseEntity<ExitResponse> exitVehicle(@PathVariable Long ticketId, @RequestBody PaymentRequest paymentRequest) {
        ExitResponse response = parkingService.unparkVehicle(ticketId, paymentRequest.getAmount());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/entry-gates")
    public ResponseEntity<List<EntryGate>> getEntryGates() {
        List<EntryGate> gates = entryGateRepository.findAll();
        return ResponseEntity.ok(gates);
    }
    
    @GetMapping("/parking-lot/{parkingLotId}/status")
    public ResponseEntity<Map<String, Object>> getParkingLotStatus(@PathVariable Long parkingLotId) {
        Map<String, Object> status = parkingService.getParkingLotStatus(parkingLotId);
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/exit/{ticketId}/retry")
    public ResponseEntity<ExitResponse> retryPayment(@PathVariable Long ticketId, @RequestBody PaymentRequest paymentRequest) {
        ExitResponse response = parkingService.retryPayment(ticketId, paymentRequest.getAmount());
        return ResponseEntity.ok(response);
    }
}
