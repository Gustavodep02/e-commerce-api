package com.example.e_commerce_api.controller;


import com.example.e_commerce_api.exception.ResourceNotFoundException;
import com.example.e_commerce_api.model.Cart;
import com.example.e_commerce_api.model.User;
import com.example.e_commerce_api.repository.UserRepository;
import com.example.e_commerce_api.service.cart.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;

    private final UserRepository userRepository;

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long cartId){
        try{
            Cart cart = cartService.getCart(cartId);
            return ResponseEntity.ok(cart);
        }catch(ResourceNotFoundException e ){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId){
            cartService.clearCart(cartId);
            return ResponseEntity.noContent().build();
    }

    @GetMapping("/{cartId}/total")
    public ResponseEntity<Double> getTotalAmount(@PathVariable Long cartId){
        try{
            Double totalAmount = cartService.getTotalPrice(cartId);
            return ResponseEntity.ok(totalAmount);
        }catch(ResourceNotFoundException e ){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping()
    public ResponseEntity<Cart> createCart(@RequestBody Long userId){
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        Cart cart = cartService.createCart(user);
        return ResponseEntity.ok(cart);
    }

}
