package com.provider.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String viewAllProducts(HttpSession session,Model model) {
	    User loggedInUser = (User) session.getAttribute("loggedInUser");

	    if (loggedInUser == null) {
	        return "redirect:/login"; // User not logged in
	    }
        List<Product> products = productService.viewAll();
        model.addAttribute("products", products);
        return "viewProductUser";
    }
	@GetMapping("/userHome")
	public String userHome(HttpSession session, Model model) {
	    User user = (User) session.getAttribute("loggedInUser");
	    if (user == null) return "redirect:/login";

	    model.addAttribute("user", user);
	    return "userHome";
	}
	@PostMapping("/cart/add/{productId}")
	public String addToCart(@PathVariable Long productId,
	                        @RequestParam int quantity,
	                        HttpSession session,
	                        RedirectAttributes redirectAttributes) {
	    // ✅ Get user from session
	    User loggedInUser = (User) session.getAttribute("loggedInUser");

	    if (loggedInUser == null) {
	        return "redirect:/login"; // User not logged in
	    }

	    try {
	        cartService.addProductToCart(loggedInUser.getEmail(), productId, quantity);
	        redirectAttributes.addFlashAttribute("success", "Product added to cart successfully!");
	    } catch (IllegalArgumentException ex) {
	        redirectAttributes.addFlashAttribute("error", ex.getMessage());
	    }

	    return "redirect:/user/products";
	}
	
	// View cart
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
    	User loggedInUser = (User) session.getAttribute("loggedInUser");
    	 if (loggedInUser == null) {
 	        return "redirect:/login"; // User not logged in
 	    }
        String username = loggedInUser.getName();
        User user = userService.findByName(username).get();
        Cart cart = cartService.getCartByUser(user).get();

        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("cart", cart);
        model.addAttribute("totalAmount", totalAmount);
        return "viewCart";
    }
    @PostMapping("/cart/placeOrder")
    public String placeOrder(HttpSession session) {
    	User loggedInUser = (User) session.getAttribute("loggedInUser");
   	 if (loggedInUser == null) {
	        return "redirect:/login"; // User not logged in
	    }
//       String username = loggedInUser.getName();
//        User user = userService.findByUsername(username);
        invoiceService.createSalesInvoice(loggedInUser); // replaced orderService
        return "redirect:/user/orders";
    }
    
    @GetMapping("/orders")
    public String viewUserOrders(Model model, HttpSession session) {
    	User loggedInUser = (User) session.getAttribute("loggedInUser");
      	 if (loggedInUser == null) {
   	        return "redirect:/login"; // User not logged in
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

        model.addAttribute("invoiceData", invoiceData);
        return "viewOrders";
    }
}
