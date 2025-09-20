package com.demo.parkinglot.repository;

import com.demo.parkinglot.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}
