package com.demo.parkinglot.dto;

import com.demo.parkinglot.enums.VehicleType;

public class ParkingRequest {

    private String plateNo;
    private String vehicleType;
    private String ownerId;
    private Long entryGateId;
    
    // Helper method to get VehicleType enum
    public VehicleType getVehicleTypeEnum() {
        return VehicleType.fromString(vehicleType);
    }

    public String getPlateNo() { return plateNo; }
    public void setPlateNo(String plateNo) { this.plateNo = plateNo; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    
    public Long getEntryGateId() { return entryGateId; }
    public void setEntryGateId(Long entryGateId) { this.entryGateId = entryGateId; }
}
