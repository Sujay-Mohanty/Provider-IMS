package com.provider.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.provider.entity.Product;
import com.provider.repository.ProductRepository;



@Service
public class ProductService {

	@Autowired
    ProductRepository repository;
	
	public Product addProduct(Product P) {
		return repository.save(P);}
	
	public List<Product> viewAll(){
		return repository.findAll();
		}
	public Optional<Product> findById(Long id) {
		return repository.findById(id);
	}
}
