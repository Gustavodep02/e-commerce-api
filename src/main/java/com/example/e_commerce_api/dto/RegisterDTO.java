package com.example.e_commerce_api.dto;

import com.example.e_commerce_api.model.UserRole;

public record RegisterDTO(String name, String email, String password) {
}
