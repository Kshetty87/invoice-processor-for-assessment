package com.eg.invoiceassessment.repository;


import com.eg.invoiceassessment.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByDueDateBeforeAndStatus(LocalDate date, Invoice.Status status);

}
