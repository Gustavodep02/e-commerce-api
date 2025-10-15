package com.example.e_commerce_api.controller;

import com.example.e_commerce_api.dto.CartItemAddDTO;
import com.example.e_commerce_api.dto.CartItemUpdateDTO;
import com.example.e_commerce_api.model.Cart;
import com.example.e_commerce_api.service.cart.ICartItemService;
import com.example.e_commerce_api.service.cart.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cartItems")
public class CartItemController {
    private final ICartItemService cartItemService;

    private final ICartService cartService;

    @PostMapping
    public ResponseEntity<Cart> addItemToCart(@RequestBody @Valid CartItemAddDTO cartItemDTO){
        cartItemService.addItemToCart(cartItemDTO.cartId(), cartItemDTO.productId(), cartItemDTO.quantity());
        var cart = cartService.getCart(cartItemDTO.cartId());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{cartId}/products/{productId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long productId){
        cartItemService.removeItemFromCart(cartId, productId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{cartId}/products/{productId}")
    public ResponseEntity<Cart> updateItemQuantity(@RequestBody @Valid CartItemUpdateDTO cartItemUpdateDTO, @PathVariable Long cartId, @PathVariable Long productId){
        cartItemService.updateItemQuantity(cartId, productId, cartItemUpdateDTO.quantity());
        var cart = cartService.getCart(cartId);
        return ResponseEntity.ok(cart);
    }
}
