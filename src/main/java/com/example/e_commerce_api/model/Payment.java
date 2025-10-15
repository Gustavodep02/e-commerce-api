package com.example.e_commerce_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private String checkoutUrl;
    private String status;
    private Long amount;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
