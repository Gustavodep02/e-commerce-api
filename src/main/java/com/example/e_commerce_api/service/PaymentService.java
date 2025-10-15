package com.example.e_commerce_api.service;

import com.example.e_commerce_api.model.Cart;
import com.example.e_commerce_api.model.Payment;
import com.example.e_commerce_api.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment savePayment(Cart cart, String sessionId, String checkoutUrl, Long amount) {
        Payment payment = Payment.builder()
                .cart(cart)
                .sessionId(sessionId)
                .checkoutUrl(checkoutUrl)
                .amount(amount)
                .status("CREATED")
                .build();

        return paymentRepository.save(payment);
    }
}
