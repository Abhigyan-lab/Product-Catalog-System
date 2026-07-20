package com.example.ProductCatalogSystem.Repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ProductCatalogSystem.DTO.ProductDto;
import com.example.ProductCatalogSystem.Entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. Safe lookup using Optional for active products
    Optional<Product> findByIdAndActiveTrue(Long id);

    // 2. Query method supporting Pagination: Get active products under a certain category
    Page<Product> findByCategoryAndActiveTrue(String category, Pageable pageable);

    // 3. Derived query: Get all active products with price between dynamic limits
    List<Product> findByPriceBetweenAndActiveTrue(Double minPrice, Double maxPrice);

    // 4. Custom Modifying Native SQL: Modify stock safely
    @Modifying
    @Query(value = "UPDATE products SET stock_quantity = :quantity WHERE id = :productId AND active = true", nativeQuery = true)
    int updateProductStock(@Param("productId") Long id, @Param("quantity") Integer quantity);

    // 5. Custom Modifying Native SQL: Soft delete a product
    @Modifying
    @Query(value = "UPDATE products SET active = false WHERE id = :productId", nativeQuery = true)
    int softDeleteProduct(@Param("productId") Long id);
}
