package com.example.e_commerce_api.service.cart;

import com.example.e_commerce_api.exception.ResourceNotFoundException;
import com.example.e_commerce_api.model.Cart;
import com.example.e_commerce_api.model.CartItem;
import com.example.e_commerce_api.model.Product;
import com.example.e_commerce_api.repository.CartItemRepository;
import com.example.e_commerce_api.repository.CartRepository;
import com.example.e_commerce_api.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartItemServiceTest {
    @Test
    @DisplayName("Should add new item to cart when product has sufficient stock")
    void addItemToCartAddsNewItemWhenProductHasSufficientStock() {
        var cartService = mock(ICartService.class);
        var productService = mock(ProductService.class);
        var cartItemRepository = mock(CartItemRepository.class);
        var cartRepository = mock(CartRepository.class);

        var cart = new Cart();
        cart.setId(1L);
        cart.setItems(new HashSet<>());
        var product = new Product();
        product.setId(10L);
        product.setQuantity(5);
        product.setPrice(20.0);

        when(cartService.getCart(1L)).thenReturn(cart);
        when(productService.getProductById(10L)).thenReturn(product);
        when(cartItemRepository.save(any(CartItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var service = new CartItemService(cartService, productService, cartItemRepository, cartRepository);
        service.addItemToCart(1L, 10L, 2);

        verify(cartItemRepository).save(any(CartItem.class));
        verify(productService).getProductById(10L);
        verifyNoMoreInteractions(productService);
        verify(cartRepository).save(cart);
        assertEquals(1, cart.getItems().size());
        var savedItem = cart.getItems().iterator().next();
        assertEquals(10L, savedItem.getProduct().getId());
        assertEquals(2, savedItem.getQuantity());
        assertEquals(20.0, savedItem.getUnitPrice());
        assertEquals(40.0, savedItem.getTotalPrice());
    }

    @Test
    @DisplayName("Should increment quantity when same product already exists in cart")
    void addItemToCartIncrementsQuantityWhenItemAlreadyExists() {
        var cartService = mock(ICartService.class);
        var productService = mock(ProductService.class);
        var cartItemRepository = mock(CartItemRepository.class);
        var cartRepository = mock(CartRepository.class);

        var cart = new Cart();
        cart.setId(1L);
        var product = new Product();
        product.setId(10L);
        product.setQuantity(10);
        product.setPrice(15.0);

        var existingItem = new CartItem();
        existingItem.setId(100L);
        existingItem.setProduct(product);
        existingItem.setQuantity(1);
        existingItem.setUnitPrice(15.0);
        existingItem.setTotalPrice();

        cart.setItems(new HashSet<>(List.of(existingItem)));

        when(cartService.getCart(1L)).thenReturn(cart);
        when(productService.getProductById(10L)).thenReturn(product);
        when(cartItemRepository.save(any(CartItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var service = new CartItemService(cartService, productService, cartItemRepository, cartRepository);
        service.addItemToCart(1L, 10L, 3);

        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(cart);
        assertEquals(1, cart.getItems().size());
        var item = cart.getItems().iterator().next();
        assertEquals(4, item.getQuantity());
        assertEquals(60.0, item.getTotalPrice());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when adding more than available stock")
    void addItemToCartThrowsWhenRequestedQuantityExceedsStock() {
        var cartService = mock(ICartService.class);
        var productService = mock(ProductService.class);
        var cartItemRepository = mock(CartItemRepository.class);
        var cartRepository = mock(CartRepository.class);

        var cart = new Cart();
        cart.setId(1L);
        cart.setItems(new HashSet<>());

        var product = new Product();
        product.setId(10L);
        product.setQuantity(1);
        product.setPrice(50.0);

        when(cartService.getCart(1L)).thenReturn(cart);
        when(productService.getProductById(10L)).thenReturn(product);

        var service = new CartItemService(cartService, productService, cartItemRepository, cartRepository);

        assertThrows(IllegalArgumentException.class, () -> service.addItemToCart(1L, 10L, 2));
        verifyNoInteractions(cartItemRepository);
        verifyNoInteractions(cartRepository);
    }

    @Test
    @DisplayName("Should remove existing item from cart and persist cart")
    void removeItemFromCartRemovesExistingItemAndSavesCart() {
        var cartService = mock(ICartService.class);
        var productService = mock(ProductService.class);
        var cartItemRepository = mock(CartItemRepository.class);
        var cartRepository = mock(CartRepository.class);

        var cart = new Cart();
        cart.setId(1L);

        var product = new Product();
        product.setId(10L);
        product.setPrice(30.0);

        var item = new CartItem();
        item.setId(200L);
        item.setProduct(product);
        item.setQuantity(1);
        item.setUnitPrice(30.0);
        item.setTotalPrice();

        cart.setItems(new HashSet<>(List.of(item)));

        when(cartService.getCart(1L)).thenReturn(cart);
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var service = new CartItemService(cartService, productService, cartItemRepository, cartRepository);
        service.removeItemFromCart(1L, 10L);

        verify(cartRepository).save(cart);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when removing non existing item")
    void removeItemFromCartThrowsWhenItemDoesNotExist() {
        var cartService = mock(ICartService.class);
        var productService = mock(ProductService.class);
        var cartItemRepository = mock(CartItemRepository.class);
        var cartRepository = mock(CartRepository.class);

        var cart = new Cart();
        cart.setId(1L);
        cart.setItems(new HashSet<>());

        when(cartService.getCart(1L)).thenReturn(cart);

        var service = new CartItemService(cartService, productService, cartItemRepository, cartRepository);

        assertThrows(ResourceNotFoundException.class,
                () -> service.removeItemFromCart(1L, 99L));
        verifyNoInteractions(cartRepository);
    }

    @Test
    @DisplayName("Should update item quantity when stock is sufficient")
    void updateItemQuantityUpdatesQuantityAndTotalPriceWhenStockSufficient() {
        var cartService = mock(ICartService.class);
        var productService = mock(ProductService.class);
        var cartItemRepository = mock(CartItemRepository.class);
        var cartRepository = mock(CartRepository.class);

        var cart = new Cart();
        cart.setId(1L);

        var product = new Product();
        product.setId(10L);
        product.setQuantity(10);
        product.setPrice(25.0);

        var item = new CartItem();
        item.setId(300L);
        item.setProduct(product);
        item.setQuantity(1);
        item.setUnitPrice(25.0);
        item.setTotalPrice();

        cart.setItems(new HashSet<>(List.of(item)));
        cart.setTotalAmount(25.0);

        when(cartService.getCart(1L)).thenReturn(cart);
        when(productService.getProductById(10L)).thenReturn(product);
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var service = new CartItemService(cartService, productService, cartItemRepository, cartRepository);
        service.updateItemQuantity(1L, 10L, 4);

        verify(cartRepository).save(cart);
        assertEquals(4, item.getQuantity());
        assertEquals(100.0, item.getTotalPrice());
        assertEquals(25.0, item.getUnitPrice());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating to quantity greater than stock")
    void updateItemQuantityThrowsWhenRequestedQuantityExceedsStock() {
        var cartService = mock(ICartService.class);
        var productService = mock(ProductService.class);
        var cartItemRepository = mock(CartItemRepository.class);
        var cartRepository = mock(CartRepository.class);

        var cart = new Cart();
        cart.setId(1L);

        var product = new Product();
        product.setId(10L);
        product.setQuantity(2);
        product.setPrice(10.0);

        var item = new CartItem();
        item.setId(400L);
        item.setProduct(product);
        item.setQuantity(1);
        item.setUnitPrice(10.0);
        item.setTotalPrice();

        cart.setItems(new HashSet<>(List.of(item)));

        when(cartService.getCart(1L)).thenReturn(cart);
        when(productService.getProductById(10L)).thenReturn(product);

        var service = new CartItemService(cartService, productService, cartItemRepository, cartRepository);

        assertThrows(IllegalArgumentException.class, () -> service.updateItemQuantity(1L, 10L, 5));
    }

    @Test
    @DisplayName("Should return cart item when it exists in cart")
    void getCartItemReturnsItemWhenPresent() {
        var cartService = mock(ICartService.class);
        var productService = mock(ProductService.class);
        var cartItemRepository = mock(CartItemRepository.class);
        var cartRepository = mock(CartRepository.class);

        var cart = new Cart();
        cart.setId(1L);

        var product = new Product();
        product.setId(50L);

        var item = new CartItem();
        item.setId(500L);
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(7.5);
        item.setTotalPrice();

        cart.setItems(new HashSet<>(List.of(item)));

        when(cartService.getCart(1L)).thenReturn(cart);

        var service = new CartItemService(cartService, productService, cartItemRepository, cartRepository);
        var result = service.getCartItem(1L, 50L);

        assertSame(item, result);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when getting non existing cart item")
    void getCartItemThrowsWhenItemNotFound() {
        var cartService = mock(ICartService.class);
        var productService = mock(ProductService.class);
        var cartItemRepository = mock(CartItemRepository.class);
        var cartRepository = mock(CartRepository.class);

        var cart = new Cart();
        cart.setId(1L);
        cart.setItems(new HashSet<>());

        when(cartService.getCart(1L)).thenReturn(cart);

        var service = new CartItemService(cartService, productService, cartItemRepository, cartRepository);

        assertThrows(ResourceNotFoundException.class,
                () -> service.getCartItem(1L, 999L));
    }
}