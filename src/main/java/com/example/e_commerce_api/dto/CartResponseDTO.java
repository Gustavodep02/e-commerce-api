package com.example.e_commerce_api.dto;

import com.example.e_commerce_api.model.CartItem;
import com.example.e_commerce_api.model.Payment;

import java.util.List;
import java.util.Set;

public record CartResponseDTO(
        Long id,
        Double totalAmount,
        Set<CartItem> items,
        List<Payment> payments,
        Long user
) {}
