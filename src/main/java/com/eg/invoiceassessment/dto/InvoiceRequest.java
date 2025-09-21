package com.eg.invoiceassessment.dto;

import com.eg.invoiceassessment.entity.Invoice;
import lombok.Data;

import java.time.LocalDate;


public record InvoiceRequest(Double amount, LocalDate dueDate) {
}

