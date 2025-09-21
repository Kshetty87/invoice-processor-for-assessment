package com.eg.invoiceassessment.service;



import com.eg.invoiceassessment.dto.InvoiceRequest;
import com.eg.invoiceassessment.dto.InvoiceResponse;
import com.eg.invoiceassessment.dto.PaymentRequest;
import com.eg.invoiceassessment.dto.ProcessOverdueInvoiceRequest;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(InvoiceRequest request);

    List<InvoiceResponse> getAllInvoices();

    InvoiceResponse payInvoice(Long invoiceId, PaymentRequest paymentRequest);

    void processOverdueInvoices(ProcessOverdueInvoiceRequest processRequest);

    InvoiceResponse getInvoiceById(Long id);
}
