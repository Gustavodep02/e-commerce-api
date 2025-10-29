package com.example.e_commerce_api.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(@NotBlank String name, @NotBlank String email, @NotBlank String password) {
}
