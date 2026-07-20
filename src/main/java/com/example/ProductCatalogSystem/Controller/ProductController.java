package com.example.ProductCatalogSystem.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ProductCatalogSystem.DTO.ProductDto;
import com.example.ProductCatalogSystem.Entity.Product;
import com.example.ProductCatalogSystem.Service.ProductServiceImpl;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
    private  ProductServiceImpl productService;

    // 1. GET Single Product
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
  

    // 2. GET Paginated & Sorted Active Product DTOs
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(productService.getAllProductsPaginated(page, size, sortBy, direction));
    }

    // 3. GET Active Product DTOs by Category
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(productService.getProductsByCategory(category, page, size));
    }

    // 4. GET Product DTOs within Price Range
    @GetMapping("/filter")
    public ResponseEntity<List<ProductDto>> getProductsByPriceRange(
            @RequestParam Double min,
            @RequestParam Double max) {
        return ResponseEntity.ok(productService.getProductsInPriceRange(min, max));
    }

    // 5. POST Create Product using ProductDTO
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDTO) {
        ProductDto createdProduct = productService.addProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // 6. PUT Complete Update using ProductDTO
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    // 7. PATCH Update Price Only
    @PatchMapping("/{id}/price")
    public ResponseEntity<ProductDto> patchPrice(@PathVariable Long id, @RequestParam Double price) {
        return ResponseEntity.ok(productService.updateProductPrice(id, price));
    }

    // 8. PATCH Restock Quantity Only
    @PatchMapping("/{id}/restock")
    public ResponseEntity<Void> restock(@PathVariable Long id, @RequestParam Integer quantity) {
        productService.restockProduct(id, quantity);
        return ResponseEntity.noContent().build();
    }

    // 9. DELETE Soft-delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}