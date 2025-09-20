package com.demo.parkinglot.repository;

import com.demo.parkinglot.entity.Payment;
import com.demo.parkinglot.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByTicket(Ticket ticket);
}
