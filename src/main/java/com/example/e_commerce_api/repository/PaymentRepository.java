package com.example.e_commerce_api.repository;

import com.example.e_commerce_api.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findBySessionId(String sessionId);
}
