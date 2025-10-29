package com.example.e_commerce_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CartItemAddDTO(Long cartId, @NotNull Long productId, @NotNull int quantity) {
}
