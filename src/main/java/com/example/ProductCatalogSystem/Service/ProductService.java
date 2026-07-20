package com.example.ProductCatalogSystem.Service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.ProductCatalogSystem.DTO.ProductDto;
import com.example.ProductCatalogSystem.Entity.Product;

public interface ProductService {
	 	ProductDto getProductById(Long id);
	    Page<ProductDto> getAllProductsPaginated(int page, int size, String sortBy, String direction);
	    Page<ProductDto> getProductsByCategory(String category, int page, int size);
	    List<ProductDto> getProductsInPriceRange(Double min, Double max);
	    ProductDto addProduct(ProductDto productDTO);
	    ProductDto updateProduct(Long id, ProductDto productDTO);
	    ProductDto updateProductPrice(Long id, Double newPrice);
	    void restockProduct(Long id, Integer quantity);
	    void deleteProduct(Long id);
}
