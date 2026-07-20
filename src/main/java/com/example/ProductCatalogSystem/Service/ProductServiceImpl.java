package com.example.ProductCatalogSystem.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ProductCatalogSystem.DTO.ProductDto;
import com.example.ProductCatalogSystem.Entity.Product;
import com.example.ProductCatalogSystem.ExceptionHandler.ResourceNotFoundException;
import com.example.ProductCatalogSystem.Repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
    private  ProductRepository productRepository;

	 // Helper: Convert Entity to DTO
    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCategory(product.getCategory());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }

	// Helper: Convert DTO to Entity
    private Product convertToEntity(ProductDto dto) {
        Product entity = new Product();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setPrice(dto.getPrice());
        entity.setStockQuantity(dto.getStockQuantity());
        return entity;
    }

    
    // READ (Single active product)
    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active product not found with ID: " + id));
        return convertToDto(product);
    }

    // READ (All products with Pagination and Sorting)
    @Override
    public Page<ProductDto> getAllProductsPaginated(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(this::convertToDto);
    }

    // READ (By Category with Pagination)
    @Override
    public Page<ProductDto> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        Page<Product> productPage = productRepository.findByCategoryAndActiveTrue(category, pageable);
        return productPage.map(this::convertToDto);
    }

    // READ (Filter by Price range)
    @Override
    public List<ProductDto> getProductsInPriceRange(Double min, Double max) {
        if (min < 0 || max < min) {
            throw new IllegalArgumentException("Invalid price boundaries provided.");
        }
        List<Product> products = productRepository.findByPriceBetweenAndActiveTrue(min, max);
        return products.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // CREATE
    @Override
    @Transactional
    public ProductDto addProduct(ProductDto productDTO) {
        if (productDTO.getPrice() == null || productDTO.getPrice() < 0) {
            throw new IllegalArgumentException("Product price must be a positive value.");
        }
        if (productDTO.getStockQuantity() == null || productDTO.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Initial stock quantity cannot be negative.");
        }
        Product entity = convertToEntity(productDTO);
        entity.setActive(true); // Ensure new product defaults as active
        Product savedEntity = productRepository.save(entity);
        return convertToDto(savedEntity);
    }

    // UPDATE (Full update using PUT)
    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDTO) {
        // Fetch to ensure it exists and is active, throws ResourceNotFoundException if missing
        Product existingProduct = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active product not found with ID: " + id));

        existingProduct.setName(productDTO.getName());
        existingProduct.setCategory(productDTO.getCategory());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStockQuantity(productDTO.getStockQuantity());

        Product savedProduct = productRepository.save(existingProduct);
        return convertToDto(savedProduct);
    }

    // PATCH (Partial update for Product price)
    @Override
    @Transactional
    public ProductDto updateProductPrice(Long id, Double newPrice) {
        if (newPrice == null || newPrice < 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        Product existingProduct = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active product not found with ID: " + id));
        
        existingProduct.setPrice(newPrice);
        Product savedProduct = productRepository.save(existingProduct);
        return convertToDto(savedProduct);
    }

    // PATCH (Update Stock via native query)
    @Override
    @Transactional
    public void restockProduct(Long id, Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("New stock capacity cannot be negative.");
        }
        int rowsUpdated = productRepository.updateProductStock(id, quantity);
        if (rowsUpdated == 0) {
            throw new ResourceNotFoundException("Restock failed: Product is inactive or ID is invalid.");
        }
    }

    // DELETE (Soft-delete using native modifying query)
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        int rowsDeleted = productRepository.softDeleteProduct(id);
        if (rowsDeleted == 0) {
            throw new ResourceNotFoundException("Deletion failed: Product was already inactive or does not exist.");
        }
    }

}


