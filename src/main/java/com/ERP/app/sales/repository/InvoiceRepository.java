package com.ERP.app.sales.repository;

import com.ERP.app.sales.data.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


  //  List<Invoice> findByPayDate(LocalDate payDate);
}
