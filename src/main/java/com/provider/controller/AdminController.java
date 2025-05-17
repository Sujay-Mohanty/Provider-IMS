package com.provider.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.provider.dto.ProductUpdateRequestDTO;
import com.provider.dto.PurchaseOrderRequestDTO;
import com.provider.dto.VendorProductDTO;
import com.provider.entity.Invoice;
import com.provider.entity.Product;
import com.provider.entity.Vendor;
import com.provider.service.InvoiceService;
import com.provider.service.ProductService;
import com.provider.service.VendorService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	ProductService productService;

	@Autowired
	VendorService vendorService;

	@Autowired
	private InvoiceService invoiceService;

	// DONE
	@PostMapping("/vendor/add/save")
	public ResponseEntity<String> addVendor(@RequestBody VendorProductDTO vpDTO) {
		Vendor vendor = vpDTO.getVendor();
		Product product = vpDTO.getProduct();

		productService.addProduct(product);
		vendor.setProduct(product);
		vendorService.addVendor(vendor);
		productService.addProduct(product); // Link product to vendor

		return ResponseEntity.status(HttpStatus.CREATED).body("Vendor and Product added successfully");
	}

	// DONE
	@GetMapping("/vendor/view")
	public ResponseEntity<List<Vendor>> getAllVendors() {
		return ResponseEntity.ok(vendorService.viewAll());
	}

	// DONE
	@PostMapping("/vendor/delete/{id}")
	public ResponseEntity<String> deleteVendor(@PathVariable Long id) {
		if (vendorService.vendorExistsById(id)) {
			vendorService.deleteById(id);
			return ResponseEntity.ok("Vendor with ID " + id + " deleted successfully");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
	}

	// CLIENT SIDE
	@GetMapping("/purchase")
	public List<Vendor> showPurchaseForm() {
		return vendorService.viewAll();
//	        model.addAttribute("vendors", vendorService.viewAll());
//	        return "purchaseOrder";
	}

	// NO RETURNING
	@PostMapping("/purchase/add")
	   public ResponseEntity<String> handlePurchaseOrder(@RequestBody PurchaseOrderRequestDTO request) {
	        try {
	            LocalDateTime purchaseDate = LocalDateTime.now();
	            invoiceService.createPurchaseInvoice(
	                request.getVendorId(),
	                request.getQuantity(),
	                request.getPrice(),
	                purchaseDate
	            );

	            return ResponseEntity.status(HttpStatus.CREATED)
	                                 .body("Purchase order created successfully");

	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body("Failed to create purchase order");
	        }
	    }

	// DONE
	@GetMapping("/vendor/{vendorId}/product")
//	@ResponseBody
	public ResponseEntity<Product> getProductByVendor(@PathVariable Long vendorId) {
		return vendorService.findById(vendorId).map(vendor -> ResponseEntity.ok(vendor.getProduct()))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	// DONE
	@GetMapping("/invoices")
	public ResponseEntity<List<Map<String, Object>>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.findAll();

        List<Map<String, Object>> invoiceData = invoices.stream().map(invoice -> {
            Map<String, Object> map = new HashMap<>();
            map.put("invoice", invoice);
            map.put("formattedDate", invoice.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

            String name = invoice.getType().equalsIgnoreCase("PURCHASE") && invoice.getVendor() != null
                    ? invoice.getVendor().getName()
                    : invoice.getUser() != null ? invoice.getUser().getName() : "N/A";

            map.put("entityName", name);

            double total = invoice.getItems().stream()
                    .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                    .sum();
            map.put("calculatedTotal", total);

            return map;
        }).toList();

        return ResponseEntity.ok(invoiceData);
    }
	
	


    @DeleteMapping("/invoice/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        if (invoiceService.findById(id).isPresent()) {
            invoiceService.deleteById(id);
            return ResponseEntity.ok("Invoice deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found");
        }
    }

	@GetMapping("/products")
	public ResponseEntity<List<Product>> viewAllProducts() {
		return ResponseEntity.ok(productService.viewAll());
	}

	@PostMapping("/products/update/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id,
                                                @RequestBody ProductUpdateRequestDTO request) {
        try {
            productService.updateProduct(id, request.getName(), request.getDescription());
            return ResponseEntity.ok("Product updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to update product");
        }
    }
}
