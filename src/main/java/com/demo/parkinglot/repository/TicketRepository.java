package com.demo.parkinglot.repository;

import com.demo.parkinglot.entity.Ticket;
import com.demo.parkinglot.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByVehicleAndActiveTrue(Vehicle vehicle);
}