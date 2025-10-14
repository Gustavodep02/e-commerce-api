package com.example.e_commerce_api.service.cart;

import com.example.e_commerce_api.model.Cart;

public interface ICartService {
    Cart getCart(Long id);
    void clearCart(Long id);
    Double getTotalPrice(Long id);
}
