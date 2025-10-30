package com.example.e_commerce_api.controller;

import com.example.e_commerce_api.model.Cart;
import com.example.e_commerce_api.model.Payment;
import com.example.e_commerce_api.model.Product;
import com.example.e_commerce_api.service.PaymentService;
import com.example.e_commerce_api.service.ProductService;
import com.example.e_commerce_api.service.cart.ICartService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "payments", description = "Endpoints for payment checkout and retrieval")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final ICartService cartService;

    private final PaymentService paymentService;

    private final ProductService productService;

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeSecretKey;
    }

    @Value("${STRIPE_SK}")
    private String stripeSecretKey;

    @PostMapping("/checkout/{cartId}")
    @Operation(summary = "Creates a Stripe checkout session for the specified cart", description = "Generates a checkout session URL for payment processing")
    @ApiResponse(responseCode = "200", description = "Checkout session created successfully")
    @ApiResponse(responseCode = "500", description = "Error creating checkout session")
    public ResponseEntity<Map<String, Object>> createCheckoutSession(@PathVariable Long cartId) {
        try {
            Cart cart = cartService.getCart(cartId);


            long totalAmount = (long) (cart.getItems().stream()
                    .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                    .sum() * 100);

            cart.getItems().forEach(item -> {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() - item.getQuantity());
                productService.saveProduct(product);
            });

            List<SessionCreateParams.LineItem> lineItems = cart.getItems().stream()
                    .map(item -> SessionCreateParams.LineItem.builder()
                            .setQuantity((long) item.getQuantity())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("brl")
                                            .setUnitAmount((long) (item.getProduct().getPrice() * 100))
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(item.getProduct().getName())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
                    ).toList();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://meusite.com/success")
                    .setCancelUrl("https://meusite.com/cancel")
                    .addAllLineItem(lineItems)
                    .setClientReferenceId(cartId.toString())
                    .build();

            Session session = Session.create(params);

            paymentService.savePayment(cart, session.getId(), session.getUrl(), totalAmount);

            Map<String, Object> response = new HashMap<>();
            response.put("checkoutUrl", session.getUrl());
            response.put("sessionId", session.getId());

            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/carts/{cartId}")
    @Operation(summary = "Retrieves all payments for the specified cart", description = "Fetches a list of payments associated with a given cart ID")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<List<Payment>> getPaymentsByCartId(@PathVariable Long cartId) {
        Cart cart = cartService.getCart(cartId);
        return ResponseEntity.ok(cart.getPayments());
    }

}


