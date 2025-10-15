package com.example.e_commerce_api.service.cart;

import com.example.e_commerce_api.model.Cart;
import com.example.e_commerce_api.model.User;

public interface ICartService {
    Cart getCart(Long id);
    void clearCart(Long id);
    Double getTotalPrice(Long id);

    Cart createCart(User user);
}
