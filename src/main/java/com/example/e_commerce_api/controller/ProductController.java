package com.example.e_commerce_api.controller;

import com.example.e_commerce_api.dto.ProductDTO;
import com.example.e_commerce_api.model.Product;
import com.example.e_commerce_api.repository.ProductRepository;
import com.example.e_commerce_api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity createProduct(@RequestBody @Valid ProductDTO productDTO){

        var product = productService.saveProduct(new Product(
                productDTO.name(),
                productDTO.description(),
                productDTO.price(),
                productDTO.quantity()
        ));
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity getProductById(@PathVariable Long id){
        var product = productService.getProductById(id);
        if(product == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDTO productDTO){
        var updatedProduct = productService.patchProduct(id, productDTO);
        if(updatedProduct == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping
    public ResponseEntity deleteProduct(@PathVariable Long id){
        var product = productService.getProductById(id);
        if(product == null){
            return ResponseEntity.notFound().build();
        }
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
