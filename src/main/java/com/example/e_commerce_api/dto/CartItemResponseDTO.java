package com.example.e_commerce_api.dto;

import com.example.e_commerce_api.model.CartItem;

import java.util.Set;

public record CartItemResponseDTO (
        Long id,
        Double totalAmount,
        Set<CartItem> items
){
}
