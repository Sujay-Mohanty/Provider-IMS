package com.provider.dto;

import com.provider.entity.Product;
import com.provider.entity.Vendor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VendorProductDTO {
	private Vendor vendor;
	private Product product;
}
