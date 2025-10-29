package com.example.e_commerce_api.service.cart;

import com.example.e_commerce_api.exception.ResourceNotFoundException;
import com.example.e_commerce_api.model.Cart;
import com.example.e_commerce_api.model.CartItem;
import com.example.e_commerce_api.model.User;
import com.example.e_commerce_api.repository.CartItemRepository;
import com.example.e_commerce_api.repository.CartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {
    @Test
    @DisplayName("Should return cart when cart exists")
    void getCartReturnsCartWhenCartExists() {
        var cartRepository = mock(CartRepository.class);
        var cart = new Cart();
        cart.setId(1L);
        var item = new CartItem();
        item.setUnitPrice(100.0);
        item.setQuantity(1);
        cart.getItems().add(item);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        var cartService = new CartService(cartRepository, mock(CartItemRepository.class));
        var result = cartService.getCart(1L);
        assertEquals(cart.getId(), result.getId());
        assertEquals(100.0, result.getTotalAmount());
    }

    @Test
    @DisplayName("Should throw exception when cart does not exist")
    void getCartThrowsExceptionWhenCartDoesNotExist() {
        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        var cartService = new CartService(cartRepository, mock(CartItemRepository.class));

        assertThrows(ResourceNotFoundException.class, () -> cartService.getCart(1L));
    }

    @Test
    @DisplayName("Should clear cart and delete all items")
    void clearCartDeletesAllItemsAndCart() {
        var cartRepository = mock(CartRepository.class);
        var cartItemRepository = mock(CartItemRepository.class);
        var cart = new Cart();
        cart.setId(1L);
        cart.setItems(new HashSet<>());
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        var cartService = new CartService(cartRepository, cartItemRepository);
        cartService.clearCart(1L);
        verify(cartItemRepository).deleteAllByCartId(1L);
        verify(cartRepository).deleteById(1L);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should return total price of cart")
    void getTotalPriceReturnsTotalAmountOfCart() {
        var cartRepository = mock(CartRepository.class);
        var cart = new Cart();
        cart.setId(1L);
        var item = new CartItem();
        item.setUnitPrice(100.0);
        item.setQuantity(2);
        cart.getItems().add(item);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        var cartService = new CartService(cartRepository, mock(CartItemRepository.class));
        var result = cartService.getTotalPrice(1L);

        assertEquals(200.0, result);
    }

    @Test
    @DisplayName("Should create and save a new cart for user")
    void createCartSavesNewCartForUser() {
        var cartRepository = mock(CartRepository.class);
        var user = new User();
        var cart = new Cart(user);
        when(cartRepository.save(org.mockito.Mockito.any(Cart.class))).thenReturn(cart);

        var cartService = new CartService(cartRepository, mock(CartItemRepository.class));
        var result = cartService.createCart(user);

        assertEquals(cart, result);
        assertEquals(user, result.getUser());
    }

}