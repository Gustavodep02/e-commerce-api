package com.example.e_commerce_api.controller;


import com.example.e_commerce_api.dto.CartResponseDTO;
import com.example.e_commerce_api.exception.ResourceNotFoundException;
import com.example.e_commerce_api.model.Cart;
import com.example.e_commerce_api.model.User;
import com.example.e_commerce_api.repository.UserRepository;
import com.example.e_commerce_api.service.cart.ICartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Tag(name= "carts", description = "Endpoints for managing shopping carts")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final ICartService cartService;

    private final UserRepository userRepository;

    @GetMapping("/{cartId}")
    @Operation(summary = "Retrieves a cart by its ID", description = "Fetches the details of a specific shopping cart using its unique identifier")
    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Cart not found")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Long cartId){
        try{
            Cart cart = cartService.getCart(cartId);
            var response = new CartResponseDTO(
                    cart.getId(),
                    cart.getTotalAmount(),
                    cart.getItems(),
                    cart.getPayments(),
                    cart.getUser().getId()
            );
            return ResponseEntity.ok(response);
        }catch(ResourceNotFoundException e ){
            return ResponseEntity.notFound().build();
        }
    }
    @Transactional
    @DeleteMapping("/{cartId}")
    @Operation(summary = "Clears all items from the cart", description = "Removes all items from the specified shopping cart")
    @ApiResponse(responseCode = "204", description = "Cart cleared successfully")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId){
            cartService.clearCart(cartId);
            return ResponseEntity.noContent().build();
    }

    @GetMapping("/{cartId}/total")
    @Operation(summary = "Calculates the total amount of the cart", description = "Returns the total price of all items in the specified shopping cart")
    @ApiResponse(responseCode = "200", description = "Total amount retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Cart not found")
    public ResponseEntity<Double> getTotalAmount(@PathVariable Long cartId){
        try{
            Double totalAmount = cartService.getTotalPrice(cartId);
            return ResponseEntity.ok(totalAmount);
        }catch(ResourceNotFoundException e ){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping()
    @Operation(summary = "Creates a new cart for a user", description = "Initializes a new shopping cart associated with the specified user")
    @ApiResponse(responseCode = "200", description = "Cart created successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<CartResponseDTO> createCart(@RequestBody Long userId){
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        Cart cart = cartService.createCart(user);
        var response = new CartResponseDTO(
                cart.getId(),
                cart.getTotalAmount(),
                cart.getItems(),
                cart.getPayments(),
                cart.getUser().getId()
        );
        return ResponseEntity.ok(response);
    }

}
