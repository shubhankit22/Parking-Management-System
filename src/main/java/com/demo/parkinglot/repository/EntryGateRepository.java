package com.demo.parkinglot.repository;

import com.demo.parkinglot.entity.EntryGate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntryGateRepository extends JpaRepository<EntryGate, Long> {
    List<EntryGate> findByParkingLotId(Long parkingLotId);
}
