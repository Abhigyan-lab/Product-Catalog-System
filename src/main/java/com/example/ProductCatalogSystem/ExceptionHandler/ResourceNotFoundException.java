package com.example.ProductCatalogSystem.ExceptionHandler;

public class ResourceNotFoundException extends RuntimeException {
	
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
