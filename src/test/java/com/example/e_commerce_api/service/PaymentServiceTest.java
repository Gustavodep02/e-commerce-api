package com.example.e_commerce_api.service;

import com.example.e_commerce_api.model.Cart;
import com.example.e_commerce_api.model.Payment;
import com.example.e_commerce_api.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    @Test
    @DisplayName("Should save payment and return repository result for valid inputs")
    void savePaymentReturnsRepositorySavedPaymentForValidInputs() {
        var paymentRepository = mock(PaymentRepository.class);
        var paymentService = new PaymentService(paymentRepository);
        var cart = new Cart();
        var expected = Payment.builder()
                .cart(cart)
                .sessionId("session-123")
                .checkoutUrl("https://checkout")
                .amount(100L)
                .status("CREATED")
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(expected);

        var result = paymentService.savePayment(cart, "session-123", "https://checkout", 100L);

        assertSame(expected, result);

        var captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        var savedArg = captor.getValue();
        assertEquals("session-123", savedArg.getSessionId());
        assertEquals("https://checkout", savedArg.getCheckoutUrl());
        assertEquals(100L, savedArg.getAmount());
        assertEquals("CREATED", savedArg.getStatus());
        assertSame(cart, savedArg.getCart());
    }

    @Test
    @DisplayName("Should propagate exception when repository save fails")
    void savePaymentPropagatesExceptionWhenRepositoryFails() {
        var paymentRepository = mock(PaymentRepository.class);
        var paymentService = new PaymentService(paymentRepository);
        var cart = new Cart();

        when(paymentRepository.save(any(Payment.class)))
                .thenThrow(new RuntimeException("database error"));

        assertThrows(RuntimeException.class, () -> paymentService.savePayment(cart, "s", "u", 50L));
    }

    @Test
    @DisplayName("Should save payment with null amount when amount is null")
    void savePaymentAllowsNullAmountAndSetsStatusCreated() {
        var paymentRepository = mock(PaymentRepository.class);
        var paymentService = new PaymentService(paymentRepository);
        var cart = new Cart();

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = paymentService.savePayment(cart, "sess", "url", null);

        assertNull(result.getAmount());
        assertEquals("CREATED", result.getStatus());
        assertEquals("sess", result.getSessionId());
        assertEquals("url", result.getCheckoutUrl());
        assertSame(cart, result.getCart());
    }


}