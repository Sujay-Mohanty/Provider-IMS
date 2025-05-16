package com.provider.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.provider.entity.Cart;
import com.provider.entity.Invoice;
import com.provider.entity.Product;
import com.provider.entity.User;
import com.provider.service.CartService;
import com.provider.service.InvoiceService;
import com.provider.service.ProductService;
import com.provider.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	ProductService productService;

	@Autowired
	CartService cartService;

	@Autowired
	UserService userService;

	@Autowired
	InvoiceService invoiceService;

	@GetMapping("/products")
	public ResponseEntity<?> viewAllProducts(@RequestHeader("X-User-Email") String email) {
		User loggedInUser = userService.findByEmail(email);
		if (loggedInUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
		}

		List<Product> products = productService.viewAll();
		return ResponseEntity.ok(products);
	}

	@GetMapping("/userHome")
	public ResponseEntity<?> userHome(@RequestHeader("X-User-Email") String email) {
		User loggedInUser = userService.findByEmail(email);
		if (loggedInUser  == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
		}

		return ResponseEntity.ok(loggedInUser);
	}

	@PostMapping("/cart/add/{productId}")
	public ResponseEntity<?> addToCart(@PathVariable Long productId, @RequestParam int quantity, @RequestHeader("X-User-Email") String email) {
		User loggedInUser = userService.findByEmail(email);
		if (loggedInUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
		}

		try {
			cartService.addProductToCart(loggedInUser.getEmail(), productId, quantity);
			return ResponseEntity.ok("Product added to cart successfully");
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}

	// View cart
	@GetMapping("/cart")
	public ResponseEntity<?> viewCart(@RequestHeader("X-User-Email") String email) {
		User loggedInUser = userService.findByEmail(email);
		if (loggedInUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
		}

		String username = loggedInUser.getName();
		Optional<User> userOpt = userService.findByName(username);
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		Optional<Cart> cartOpt = cartService.getCartByUser(userOpt.get());
		if (cartOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found");
		}

		Cart cart = cartOpt.get();
		double totalAmount = cart.getItems().stream()
				.mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();

		Map<String, Object> response = new HashMap<>();
		response.put("cart", cart);
		response.put("totalAmount", totalAmount);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/cart/orders")
    public ResponseEntity<?> placeOrder(@RequestHeader("X-User-Email") String email) {
        User loggedInUser = userService.findByEmail(email);
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        invoiceService.createSalesInvoice(loggedInUser);
        return ResponseEntity.ok("Order placed successfully");
    }


    // ✅ View User Orders
    @GetMapping("/orders")
    public ResponseEntity<?> viewUserOrders(@RequestHeader("X-User-Email") String email) {
        User loggedInUser = userService.findByEmail(email);
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        List<Invoice> salesInvoices = invoiceService.findByUserAndType(loggedInUser, "SALES");
        List<Map<String, Object>> invoiceData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        for (Invoice invoice : salesInvoices) {
            Map<String, Object> map = new HashMap<>();
            map.put("invoice", invoice);
            map.put("formattedDate", invoice.getDateTime().format(formatter));
            invoiceData.add(map);
        }

        return ResponseEntity.ok(invoiceData);
    }
}
