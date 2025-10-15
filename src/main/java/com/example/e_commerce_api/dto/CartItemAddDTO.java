package com.example.e_commerce_api.dto;

import jakarta.validation.constraints.NotBlank;


public record CartItemAddDTO(Long cartId, @NotBlank Long productId, @NotBlank int quantity) {
}
