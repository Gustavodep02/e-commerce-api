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
    public ResponseEntity<List<Payment>> getPaymentsByCartId(@PathVariable Long cartId) {
        Cart cart = cartService.getCart(cartId);
        return ResponseEntity.ok(cart.getPayments());
    }

}


