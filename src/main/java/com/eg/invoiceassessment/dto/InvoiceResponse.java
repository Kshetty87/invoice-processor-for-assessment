package com.eg.invoiceassessment.dto;

import java.time.LocalDate;

public record InvoiceResponse(Long id, Double amount, Double paidAmount,
                              LocalDate duedate, String status) {
}

