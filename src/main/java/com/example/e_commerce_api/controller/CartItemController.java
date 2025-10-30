package com.example.e_commerce_api.controller;

import com.example.e_commerce_api.dto.CartItemAddDTO;
import com.example.e_commerce_api.dto.CartItemResponseDTO;
import com.example.e_commerce_api.dto.CartItemUpdateDTO;
import com.example.e_commerce_api.model.Cart;
import com.example.e_commerce_api.service.cart.ICartItemService;
import com.example.e_commerce_api.service.cart.ICartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cartItems")
@SecurityRequirement(name = "bearerAuth")
@Tag(name= "cart-items", description = "Endpoints for managing items in the shopping cart")
public class CartItemController {
    private final ICartItemService cartItemService;

    private final ICartService cartService;

    @PostMapping
    @Operation(summary = "Adds an item to the cart", description = "Adds a specified product with a given quantity to the shopping cart")
    @ApiResponse(responseCode = "200", description = "Item added to cart successfully")
    public ResponseEntity<CartItemResponseDTO> addItemToCart(@RequestBody @Valid CartItemAddDTO cartItemDTO){
        cartItemService.addItemToCart(cartItemDTO.cartId(), cartItemDTO.productId(), cartItemDTO.quantity());
        var cart = cartService.getCart(cartItemDTO.cartId());
        var response = new CartItemResponseDTO(
                cart.getId(),
                cartService.getTotalPrice(cartItemDTO.cartId()),
                cart.getItems()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cartId}/products/{productId}")
    @Operation(summary = "Removes an item from the cart", description = "Removes a specified product from the shopping cart")
    @ApiResponse(responseCode = "200", description = "Item removed from cart successfully")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long productId){
        cartItemService.removeItemFromCart(cartId, productId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{cartId}/products/{productId}")
    @Operation(summary = "Updates the quantity of an item in the cart", description = "Modifies the quantity of a specified product in the shopping cart")
    @ApiResponse(responseCode = "200", description = "Item quantity updated successfully")
    public ResponseEntity<CartItemResponseDTO> updateItemQuantity(@RequestBody @Valid CartItemUpdateDTO cartItemUpdateDTO, @PathVariable Long cartId, @PathVariable Long productId){
        cartItemService.updateItemQuantity(cartId, productId, cartItemUpdateDTO.quantity());
        var cart = cartService.getCart(cartId);
        var response = new CartItemResponseDTO(
                cart.getId(),
                cartService.getTotalPrice(cartId),
                cart.getItems()
        );
        return ResponseEntity.ok(response);
    }
}
