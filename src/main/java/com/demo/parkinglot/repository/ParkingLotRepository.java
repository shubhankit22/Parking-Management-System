package com.demo.parkinglot.repository;

import com.demo.parkinglot.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    
    List<ParkingLot> findByIsActiveTrue();
    
    Optional<ParkingLot> findByNameAndLocation(String name, String location);
    
    @Query("SELECT pl FROM ParkingLot pl WHERE pl.isActive = true AND pl.id = :id")
    Optional<ParkingLot> findActiveById(@Param("id") Long id);
    
    @Query("SELECT COUNT(pl) FROM ParkingLot pl WHERE pl.isActive = true")
    long countActiveParkingLots();
}