package com.eg.invoiceassessment.service;



import com.eg.invoiceassessment.dto.InvoiceRequest;
import com.eg.invoiceassessment.dto.InvoiceResponse;
import com.eg.invoiceassessment.dto.PaymentRequest;
import com.eg.invoiceassessment.dto.ProcessOverdueInvoiceRequest;
import com.eg.invoiceassessment.entity.Invoice;
import com.eg.invoiceassessment.exception.InvalidRequestPayloadException;
import com.eg.invoiceassessment.exception.ResourceNotFoundException;
import com.eg.invoiceassessment.mapper.InvoiceMapper;
import com.eg.invoiceassessment.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }



    @Override
    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        Invoice invoice = invoiceRepository.save(InvoiceMapper.toEntity(request));
        return new InvoiceResponse(invoice.getId(),invoice.getAmount(),
                invoice.getPaidAmount(),invoice.getDueDate(), invoice.getStatus().name());
    }


    @Override
    public List<InvoiceResponse> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        // If no invoices are found, return an empty list
        if (invoices.isEmpty()) {
            return List.of();
        }
        // Convert list of entities to list of response DTOs
        return invoices.stream()
                .map(InvoiceMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public InvoiceResponse payInvoice(Long id, PaymentRequest paymentRequest) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));

        double remainingAmount = invoice.getAmount() - invoice.getPaidAmount();

        if (paymentRequest.amount() > remainingAmount) {
            throw new InvalidRequestPayloadException("Payment amount exceeds the remaining balance");
        }

        invoice.setPaidAmount(invoice.getPaidAmount() + paymentRequest.amount());

        if (invoice.getPaidAmount() >= invoice.getAmount()) {
            invoice.setStatus(Invoice.Status.PAID);
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return InvoiceMapper.toResponse(updatedInvoice);
    }


    @Transactional
    @Override
        public void processOverdueInvoices(ProcessOverdueInvoiceRequest request) {
        List<Invoice> overdueInvoices = invoiceRepository
                .findByDueDateBeforeAndStatus(LocalDate.now(), Invoice.Status.PENDING);

        for(Invoice invoice : overdueInvoices) {

            if (invoice.getPaidAmount() > 0 ) {
                invoice.setStatus(Invoice.Status.PAID);
                invoice.setPaidOn(LocalDateTime.now());
            }
            if (invoice.getPaidAmount() == 0.0) {
                invoice.setStatus(Invoice.Status.VOID);
            }

            invoiceRepository.save(invoice);

            Invoice newInvoice = new Invoice();
            newInvoice.setAmount(invoice.getAmount() - invoice.getPaidAmount() + request.lateFees());
            newInvoice.setDueDate(LocalDate.now().plusDays(request.overduedays()));
            invoiceRepository.save(newInvoice);

        }
    }

    @Override
    public InvoiceResponse getInvoiceById(Long id) {

        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        return InvoiceMapper.toResponse(invoice);

    }


}
