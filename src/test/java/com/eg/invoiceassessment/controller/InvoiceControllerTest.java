package com.eg.invoiceassessment.controller;


import com.eg.invoiceassessment.dto.*;
import com.eg.invoiceassessment.exception.InvalidRequestPayloadException;
import com.eg.invoiceassessment.exception.ResourceNotFoundException;
import com.eg.invoiceassessment.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetInvoice_Success() {
        InvoiceResponse invoice = new InvoiceResponse(1L, 100.0,0.0, LocalDate.now(), "PENDING");

        when(invoiceService.getInvoiceById(1L)).thenReturn(invoice);

        ResponseEntity<ResponseDTO<InvoiceResponse>> response = invoiceController.getInvoice(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(invoice, response.getBody().getData());
    }

    @Test
    void testGetInvoice_NotFound() {
        when(invoiceService.getInvoiceById(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> invoiceController.getInvoice(1L));
    }

    @Test
    void testCreateInvoice_Success() {
        InvoiceRequest request = new InvoiceRequest(200.0, LocalDate.now());
        InvoiceResponse created = new InvoiceResponse(1L, 200.0,0.0, LocalDate.now(), "PENDING");

        when(invoiceService.createInvoice(request)).thenReturn(created);

        ResponseEntity<ResponseDTO<String>> response = invoiceController.createInvoice(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("1", response.getBody().getData());
        assertEquals("SUCCESS", response.getBody().getMessage());
    }

    @Test
    void testCreateInvoice_InvalidRequest() {
        InvoiceRequest request = new InvoiceRequest(null, null);

        assertThrows(InvalidRequestPayloadException.class,
                () -> invoiceController.createInvoice(request));
    }

    @Test
    void testGetAllInvoices_Success() {
        List<InvoiceResponse> invoices = List.of(
                new InvoiceResponse(1L, 100.0, 0.0,LocalDate.now(), "PENDING"),
                new InvoiceResponse(2L, 200.0, 0.0, LocalDate.now(), "PENDING")
        );

        when(invoiceService.getAllInvoices()).thenReturn(invoices);

        ResponseEntity<ResponseDTO<List<InvoiceResponse>>> response = invoiceController.getAllInvoices();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getData().size());
    }

    @Test
    void testGetAllInvoices_NotFound() {
        when(invoiceService.getAllInvoices()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> invoiceController.getAllInvoices());
    }

    @Test
    void testPayInvoice_Success() {
        PaymentRequest request = new PaymentRequest(100.0);
        InvoiceResponse paidInvoice = new InvoiceResponse(1L, 100.0,0.0, LocalDate.now(), "PAID");

        when(invoiceService.payInvoice(1L, request)).thenReturn(paidInvoice);

        ResponseEntity<ResponseDTO<InvoiceResponse>> response = invoiceController.payInvoice(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paidInvoice, response.getBody().getData());
    }

    @Test
    void testPayInvoice_BadRequest() {
        PaymentRequest request = new PaymentRequest(0.0);

        ResponseEntity<ResponseDTO<InvoiceResponse>> response = invoiceController.payInvoice(1L, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testProcessOverdueInvoices_Success() {
        ProcessOverdueInvoiceRequest request = new ProcessOverdueInvoiceRequest(10.0, 5);

        ResponseEntity<ResponseDTO<String>> response = invoiceController.processOverdueInvoices(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Overdue invoices processed successfully", response.getBody().getData());

        verify(invoiceService).processOverdueInvoices(request);
    }

    @Test
    void testProcessOverdueInvoices_BadRequest() {
        ProcessOverdueInvoiceRequest request = new ProcessOverdueInvoiceRequest(0.0, 0);

        ResponseEntity<ResponseDTO<String>> response = invoiceController.processOverdueInvoices(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid late fee or overdue days", response.getBody().getData());
    }
}
