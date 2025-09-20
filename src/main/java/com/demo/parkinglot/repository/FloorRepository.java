package com.demo.parkinglot.repository;

import com.demo.parkinglot.entity.Floor;
import com.demo.parkinglot.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FloorRepository extends JpaRepository<Floor, Long> {
    
    List<Floor> findByParkingLotOrderByFloorNumberAsc(ParkingLot parkingLot);
    
    List<Floor> findByParkingLotAndIsActiveTrueOrderByFloorNumberAsc(ParkingLot parkingLot);
    
    Optional<Floor> findByParkingLotAndFloorNumber(ParkingLot parkingLot, int floorNumber);
    
    @Query("SELECT f FROM Floor f WHERE f.parkingLot = :parkingLot AND f.availableSlots > 0 ORDER BY f.floorNumber ASC")
    List<Floor> findFloorsWithAvailableSlots(@Param("parkingLot") ParkingLot parkingLot);
    
    @Query("SELECT f FROM Floor f WHERE f.parkingLot = :parkingLot AND f.availableSlots > 0 AND f.isActive = true ORDER BY f.floorNumber ASC")
    List<Floor> findActiveFloorsWithAvailableSlots(@Param("parkingLot") ParkingLot parkingLot);
    
    @Query("SELECT COUNT(f) FROM Floor f WHERE f.parkingLot = :parkingLot AND f.availableSlots > 0")
    long countFloorsWithAvailableSlots(@Param("parkingLot") ParkingLot parkingLot);
    
    @Query("SELECT SUM(f.availableSlots) FROM Floor f WHERE f.parkingLot = :parkingLot AND f.isActive = true")
    Integer getTotalAvailableSlots(@Param("parkingLot") ParkingLot parkingLot);
}
