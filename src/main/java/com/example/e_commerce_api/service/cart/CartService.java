package com.example.e_commerce_api.service.cart;

import com.example.e_commerce_api.exception.ResourceNotFoundException;
import com.example.e_commerce_api.model.Cart;
import com.example.e_commerce_api.model.User;
import com.example.e_commerce_api.repository.CartItemRepository;
import com.example.e_commerce_api.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Cart getCart(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart with id " + id + " not found"));
    }

    @Override
    public void clearCart(Long id) {
        Cart cart = getCart(id);
        cartItemRepository.deleteAllByCartId(id);
        cart.getItems().clear();
        cartRepository.deleteById(id);
    }

    @Override
    public Double getTotalPrice(Long id) {
        Cart cart = getCart(id);
        return cart.getTotalAmount();
    }

    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart(user);
        return cartRepository.save(cart);
    }
}
