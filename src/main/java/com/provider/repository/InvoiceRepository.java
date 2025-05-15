package com.provider.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.provider.entity.Invoice;
import com.provider.entity.User;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {

	List<Invoice> findAllByUserAndType(User user, String type);

}
