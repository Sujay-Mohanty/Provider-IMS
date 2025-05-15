package com.provider.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.provider.entity.Cart;
import com.provider.entity.Product;
import com.provider.entity.User;

public interface CartRepository extends JpaRepository<Cart,Long> {
//	Optional<Cart> findByUserAndProduct(User user, Product product);
	//Old Logic Code
	
	Optional<Cart> findByUser(User user);
}
