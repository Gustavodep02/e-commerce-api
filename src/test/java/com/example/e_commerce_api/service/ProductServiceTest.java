package com.example.e_commerce_api.service;

import com.example.e_commerce_api.dto.ProductDTO;
import com.example.e_commerce_api.model.Product;
import com.example.e_commerce_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    @DisplayName("Should return all products when repository is not empty")
    void getAllProductsReturnsAllProducts() {
        var products = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(products);

        var result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals(products, result);
    }

    @Test
    @DisplayName("Should save and return the product")
    void saveProductReturnsSavedProduct() {
        var product = new Product();
        when(productRepository.save(product)).thenReturn(product);

        var result = productService.saveProduct(product);

        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    @DisplayName("Should return product by ID when it exists")
    void getProductByIdReturnsProductIfExists() {
        var product = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    @DisplayName("Should return null when product ID does not exist")
    void getProductByIdReturnsNullIfNotExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        var result = productService.getProductById(1L);

        assertNull(result);
    }

    @Test
    @DisplayName("Should update and return the patched product")
    void patchProductUpdatesAndReturnsProduct() {
        var existingProduct = new Product();
        var productDTO = new ProductDTO("Updated Name", "Updated Description", 100.0, 10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        var result = productService.patchProduct(1L, productDTO);

        assertNotNull(result);
        assertEquals("Updated Name", existingProduct.getName());
        assertEquals("Updated Description", existingProduct.getDescription());
        assertEquals(100.0, existingProduct.getPrice());
        assertEquals(10, existingProduct.getQuantity());
    }

    @Test
    @DisplayName("Should return null when patching a non-existent product")
    void patchProductReturnsNullIfProductNotExists() {
        var productDTO = new ProductDTO("Name", "Description", 50.0, 5);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        var result = productService.patchProduct(1L, productDTO);

        assertNull(result);
    }

    @Test
    @DisplayName("Should delete product by ID")
    void deleteProductDeletesProduct() {
        productService.deleteProduct(1L);

        assertDoesNotThrow(() -> productRepository.deleteById(1L));
    }
}