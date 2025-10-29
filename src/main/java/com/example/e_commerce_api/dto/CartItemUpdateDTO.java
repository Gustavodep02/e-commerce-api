package com.example.e_commerce_api.dto;

import jakarta.validation.constraints.NotNull;

public record CartItemUpdateDTO(@NotNull int quantity) {
}
