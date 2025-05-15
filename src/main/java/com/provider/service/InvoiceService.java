package com.provider.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.provider.entity.Cart;
import com.provider.entity.CartItem;
import com.provider.entity.Invoice;
import com.provider.entity.InvoiceItem;
import com.provider.entity.Product;
import com.provider.entity.User;
import com.provider.entity.Vendor;
import com.provider.repository.CartRepository;
import com.provider.repository.InvoiceRepository;
import com.provider.repository.ProductRepository;
import com.provider.repository.VendorRepository;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VendorRepository vendorRepository;
    
    @Autowired
    private CartRepository cartRepository;

    public void createPurchaseInvoice(Long vendorId, int quantity, double price, LocalDateTime purchaseDate) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Product product = vendor.getProduct();
        if (product == null) {
            throw new RuntimeException("No product associated with the selected vendor");
        }

        // Update product quantity and price
        double tempQty=product.getQuantity();
        double tempPrice=product.getPrice();
        double totAmount=tempQty*tempPrice;
        totAmount+=quantity*price;
        
        product.setQuantity(product.getQuantity() + quantity);
        product.setPrice(totAmount/product.getQuantity()); // assuming price update is part of vendor supplying it
        productRepository.save(product);

        // Create Invoice
        Invoice invoice = new Invoice();
        invoice.setType("PURCHASE");
        invoice.setVendor(vendor);
        invoice.setDateTime(purchaseDate);
        invoice.setPrice(quantity * price); // total invoice price

        // Create InvoiceItem
        InvoiceItem item = new InvoiceItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(price);
        item.setInvoice(invoice); // associate with invoice

        // Add item to invoice
        invoice.setItems(Collections.singletonList(item));

        // Save invoice (cascade will save InvoiceItem too)
        invoiceRepository.save(invoice);
    }
    public void createSalesInvoice(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + user.getName()));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place order.");
        }

        Invoice invoice = new Invoice();
        invoice.setType("SALES");
        invoice.setUser(user);
        invoice.setDateTime(LocalDateTime.now());

        List<InvoiceItem> invoiceItems = new ArrayList<>();
        double totalPrice = 0.0;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            //LOGICAL FLAW: ALREADY REDUCED QUANTITY WHEN ADDED TO CART
            
//            if (product.getQuantity() < quantity) {
//                throw new RuntimeException("Insufficient stock for product: " + product.getName());
//            }
//
//            // Reduce stock
//            product.setQuantity(product.getQuantity() - quantity);
//            productRepository.save(product);

            InvoiceItem item = new InvoiceItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPrice(product.getPrice());
            item.setInvoice(invoice);

            invoiceItems.add(item);
            totalPrice += product.getPrice() * quantity;
        }

        invoice.setPrice(totalPrice);
        invoice.setItems(invoiceItems);

        invoiceRepository.save(invoice); // Cascade saves InvoiceItem too

        // Clear the cart
        cart.getItems().clear();
        cartRepository.save(cart);
    }


	public List<Invoice> findAll() {
		return invoiceRepository.findAll();
	}
	public void deleteById(long id) {
		invoiceRepository.deleteById(id);
	}
	public List<Invoice> findByUserAndType(User user, String type) {
		return invoiceRepository.findAllByUserAndType( user,type);

	}
	public Optional<Invoice> findById(Long id) {
		return invoiceRepository.findById(id);
	}
}
