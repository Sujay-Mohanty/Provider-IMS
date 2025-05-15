package com.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PurchaseOrderRequestDTO {
    private Long vendorId;
    private int quantity;
    private double price;
}
