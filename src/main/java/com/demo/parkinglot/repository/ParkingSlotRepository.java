package com.demo.parkinglot.repository;

import com.demo.parkinglot.entity.ParkingSlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.available = true AND ps.slotType = :slotType ORDER BY ps.floor ASC")
    List<ParkingSlot> findAvailableSlotsByType(@Param("slotType") String slotType);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.available = true AND ps.slotType = :slotType")
    List<ParkingSlot> findAllAvailableSlotsByType(@Param("slotType") String slotType);
}
