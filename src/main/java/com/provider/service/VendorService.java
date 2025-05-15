package com.provider.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.provider.entity.Vendor;
import com.provider.repository.VendorRepository;

@Service
public class VendorService {

	@Autowired
	VendorRepository vendorRepository;
	
	public void addVendor(Vendor vendor) {
		vendorRepository.save(vendor);
		
	}
	public Vendor findVendorByName(String vendorName) {
		return vendorRepository.findByName(vendorName);
	}
	public List<Vendor> viewAll(){
		return vendorRepository.findAll();
	}
	public Optional<Vendor> findById(Long vendorId) {
		return vendorRepository.findById(vendorId);
	}
    public boolean vendorExistsById(Long id) {
        return vendorRepository.existsById(id);
    }
    public void deleteById(Long id) {
    	vendorRepository.deleteById(id);
    }

}
