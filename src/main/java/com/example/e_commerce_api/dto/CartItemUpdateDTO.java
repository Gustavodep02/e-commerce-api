package com.example.e_commerce_api.dto;

import jakarta.validation.constraints.NotBlank;

public record CartItemUpdateDTO(@NotBlank int quantity) {
}
