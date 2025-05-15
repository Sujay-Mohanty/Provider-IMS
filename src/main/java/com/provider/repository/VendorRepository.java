package com.provider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.provider.entity.Vendor;

@Repository
public interface VendorRepository extends JpaRepository<Vendor,Long>{
	public Vendor findByName(String name);

}
