package com.demo.parkinglot.repository;

import com.demo.parkinglot.entity.ParkingSlot;
import com.demo.parkinglot.entity.ParkingLot;
import com.demo.parkinglot.entity.Floor;
import com.demo.parkinglot.enums.VehicleType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.available = true AND ps.slotType = :slotType ORDER BY ps.floor ASC")
    List<ParkingSlot> findAvailableSlotsByType(@Param("slotType") VehicleType slotType);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.available = true AND ps.slotType = :slotType")
    List<ParkingSlot> findAllAvailableSlotsByType(@Param("slotType") VehicleType slotType);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.available = true AND ps.slotType = :slotType AND ps.parkingLot = :parkingLot ORDER BY ps.floor ASC")
    List<ParkingSlot> findAvailableSlotsByTypeAndParkingLot(@Param("slotType") VehicleType slotType, @Param("parkingLot") ParkingLot parkingLot);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.available = true AND ps.slotType = :slotType AND ps.floor = :floor ORDER BY ps.id ASC")
    List<ParkingSlot> findAvailableSlotsByTypeAndFloor(@Param("slotType") VehicleType slotType, @Param("floor") int floor);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.id = :slotId AND ps.available = true")
    Optional<ParkingSlot> findByIdAndAvailableTrue(@Param("slotId") Long slotId);
    
    @Modifying
    @Transactional
    @Query("UPDATE ParkingSlot ps SET ps.available = false WHERE ps.id = :slotId AND ps.available = true")
    int allocateSlot(@Param("slotId") Long slotId);
    
    @Modifying
    @Transactional
    @Query("UPDATE ParkingSlot ps SET ps.available = true WHERE ps.id = :slotId")
    int freeSlot(@Param("slotId") Long slotId);
    
    @Query("SELECT COUNT(ps) FROM ParkingSlot ps WHERE ps.parkingLot = :parkingLot AND ps.slotType = :slotType AND ps.available = true")
    long countAvailableSlotsByTypeAndParkingLot(@Param("parkingLot") ParkingLot parkingLot, @Param("slotType") VehicleType slotType);
    
    @Query("SELECT COUNT(ps) FROM ParkingSlot ps WHERE ps.parkingLot = :parkingLot AND ps.available = true")
    long countTotalAvailableSlots(@Param("parkingLot") ParkingLot parkingLot);
    
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.parkingLot = :parkingLot AND ps.floor = :floor AND ps.slotType = :slotType")
    List<ParkingSlot> findByParkingLotAndFloorAndSlotType(@Param("parkingLot") ParkingLot parkingLot, @Param("floor") int floor, @Param("slotType") VehicleType slotType);
    
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.parkingLot = :parkingLot AND ps.floor = :floor")
    List<ParkingSlot> findByParkingLotAndFloor(@Param("parkingLot") ParkingLot parkingLot, @Param("floor") int floor);
    
    long countByParkingLotAndSlotType(ParkingLot parkingLot, VehicleType slotType);
    
    long countByAvailableTrue();
    
    long countBySlotType(VehicleType slotType);
    
    long countBySlotTypeAndAvailableTrue(VehicleType slotType);
    
    long countByParkingLot(ParkingLot parkingLot);
    
    long countByParkingLotAndAvailableTrue(ParkingLot parkingLot);
}
