package com.provider.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.provider.entity.Cart;
import com.provider.entity.CartItem;
import com.provider.entity.Product;
import com.provider.entity.User;
import com.provider.exceptions.UserNotFoundException;
import com.provider.repository.CartRepository;
import com.provider.repository.ProductRepository;
import com.provider.repository.UserRepository;

@Service
public class CartService {


	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	public void addProductToCart(String email, Long productId, int quantity) {
	    // 1. Fetch user and product from repositories
	    User user = userRepository.findByEmail(email)
	                 .orElseThrow(() -> new UserNotFoundException("User not found"));

	    Product product = productRepository.findById(productId)
	                     .orElseThrow(() -> new IllegalArgumentException("Product not found"));

	    // 2. Validate quantity
	    if (quantity <= 0 || quantity > product.getQuantity()) {
	        throw new IllegalArgumentException("Invalid quantity selected");
	    }

	    // 3. Fetch or create the user's cart
	    Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
	        Cart newCart = new Cart();
	        newCart.setUser(user);
	        return cartRepository.save(newCart);
	    });

	    // 4. Check if product is already in the cart
	    Optional<CartItem> existingItemOpt = cart.getItems().stream()
	    		.filter(item -> item.getProduct().getId() == productId)
	        .findFirst();

	    if (existingItemOpt.isPresent()) {
	        CartItem existingItem = existingItemOpt.get();
	        existingItem.setQuantity(existingItem.getQuantity() + quantity);
	    } else {
	        CartItem newItem = new CartItem();
	        newItem.setCart(cart);
	        newItem.setProduct(product);
	        newItem.setQuantity(quantity);
	        cart.getItems().add(newItem);
	    }
	 // Reduce product quantity
	    product.setQuantity(product.getQuantity() - quantity);
	    productRepository.save(product);

	    cartRepository.save(cart); // cascades to CartItems if set up
	}

	public Optional<Cart> getCartByUser(User user) {
		// TODO Auto-generated method stub
		return cartRepository.findByUser(user);
	}
}
