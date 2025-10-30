package com.example.e_commerce_api.controller;

import com.example.e_commerce_api.dto.ProductDTO;
import com.example.e_commerce_api.model.Product;
import com.example.e_commerce_api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
@Tag(name= "products", description = "Endpoints for product management")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Inserts a new product into the system", description = "Creates a new product with the provided details")
    @ApiResponse(responseCode = "200", description = "Product created successfully")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductDTO productDTO){

        var product = productService.saveProduct(new Product(
                productDTO.name(),
                productDTO.description(),
                productDTO.price(),
                productDTO.quantity()
        ));
        return ResponseEntity.ok(product);
    }

    @GetMapping
    @Operation(summary = "Retrieves all products", description = "Fetches a list of all products available in the system")
    @ApiResponse(responseCode = "200", description = "List of products retrieved successfully")
    public ResponseEntity<List<Product>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieves a product by its ID", description = "Fetches the details of a specific product using its unique identifier")
    @ApiResponse(responseCode = "200", description = "Product retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        var product = productService.getProductById(id);
        if(product == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Updates an existing product", description = "Modifies the details of an existing product identified by its ID")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDTO productDTO){
        var updatedProduct = productService.patchProduct(id, productDTO);
        if(updatedProduct == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a product by its ID", description = "Removes a specific product from the system using its unique identifier")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        var product = productService.getProductById(id);
        if(product == null){
            return ResponseEntity.notFound().build();
        }
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
