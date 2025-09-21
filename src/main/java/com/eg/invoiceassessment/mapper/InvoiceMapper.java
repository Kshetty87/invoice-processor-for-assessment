package com.eg.invoiceassessment.mapper;

import com.eg.invoiceassessment.dto.InvoiceRequest;
import com.eg.invoiceassessment.dto.InvoiceResponse;
import com.eg.invoiceassessment.entity.Invoice;

import java.time.LocalDate;

public class InvoiceMapper {

    public static Invoice toEntity(InvoiceRequest invoiceRequest) {
        return new Invoice(invoiceRequest.amount(), invoiceRequest.dueDate());
    }

    public static InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(invoice.getId(), invoice.getAmount(), invoice.getPaidAmount(),
                invoice.getDueDate(), invoice.getStatus().name());
    }

}
