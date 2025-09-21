package com.eg.invoiceassessment.controller;

import com.eg.invoiceassessment.dto.*;
import com.eg.invoiceassessment.exception.ErrorResponse;
import com.eg.invoiceassessment.exception.InvalidRequestPayloadException;
import com.eg.invoiceassessment.exception.ResourceNotFoundException;
import com.eg.invoiceassessment.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);
    private final InvoiceService invoiceService;
    private static final String SUCCESS = "SUCCESS";
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Operation(summary = "Fetch Invoice Details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice Details Fetched Successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "No Invoices Found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<InvoiceResponse>> getInvoice(@PathVariable Long id) {
        InvoiceResponse invoice = invoiceService.getInvoiceById(id);
        if (invoice == null ) {
            logger.warn("No invoice found.");
            throw new ResourceNotFoundException("No Invoice found");
        }

        final ResponseDTO<InvoiceResponse> resp = new ResponseDTO<>();
        resp.setData(invoice);
        resp.setMessage(SUCCESS);

        return ResponseEntity.ok(resp);
    }


    @Operation(
            summary = "Create a new Invoice",
            description = "Creates a new invoice with amount and due date. Returns the created invoice ID.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Invoice created successfully",
                            content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input payload",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<ResponseDTO<String>> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        if (request.amount() == null || request.dueDate() == null) {
            logger.warn("Bad request: Amount or Due Date is null");
            throw new InvalidRequestPayloadException("Bad request: Amount or Due Date is null");
        }
        InvoiceResponse createdInvoice = invoiceService.createInvoice(request);
        logger.info("Invoice created with ID: {}", createdInvoice.id());

        final ResponseDTO<String> resp = new ResponseDTO<>();
        resp.setData(createdInvoice.id().toString());
        resp.setMessage(SUCCESS);

        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }


    // Get all invoices
    @Operation(
            summary = "Get all invoices",
            description = "Returns a list of all invoices. Throws ResourceNotFoundException if none are found.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseDTO.class)))),
                    @ApiResponse(responseCode = "404", description = "No invoices found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<ResponseDTO<List<InvoiceResponse>>> getAllInvoices() {
        List<InvoiceResponse> invoices = invoiceService.getAllInvoices();
        if (invoices.isEmpty()) {
            logger.warn("No invoices found.");

            throw new ResourceNotFoundException("No Invoices found");
        }

        final ResponseDTO<List<InvoiceResponse>> resp = new ResponseDTO<>();
        resp.setData(invoices);
        resp.setMessage(SUCCESS);
        return ResponseEntity.ok(resp);
    }




    @Operation(
            summary = "Process payment for an invoice",
            description = "Processes a payment for the given invoice ID and returns the updated invoice.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment processed successfully",
                            content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid payment amount",
                            content = @Content),
                    @ApiResponse(responseCode = "404", description = "Invoice not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/{id}/payments")
    public ResponseEntity<ResponseDTO<InvoiceResponse>> payInvoice(@PathVariable Long id, @RequestBody PaymentRequest request) {
        if (request.amount() == null || request.amount() <= 0) {
            logger.warn("Invalid payment amount for invoice ID: {}", id);
            throw new InvalidRequestPayloadException("Invalid payment amount");
        }
        InvoiceResponse paidInvoice = invoiceService.payInvoice(id, request);
        logger.info("Payment processed for invoice ID: {}", paidInvoice.id());


        final ResponseDTO<InvoiceResponse> resp = new ResponseDTO<>();
        resp.setData(paidInvoice);
        resp.setMessage(SUCCESS);

        return ResponseEntity.ok(resp); // 200 OK
    }


    // Process overdue invoices
    @Operation(
            summary = "Process overdue invoices",
            description = "Applies late fees to overdue invoices based on the provided late fee and overdue days.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Overdue invoices processed successfully",
                            content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/process-overdue")
    public ResponseEntity<ResponseDTO<String>> processOverdueInvoices(@RequestBody ProcessOverdueInvoiceRequest request) {
        final ResponseDTO<String> resp = new ResponseDTO<>();

        if (request.lateFees() == null || request.lateFees() <= 0 || request.overduedays() <= 0) {
            logger.warn("Invalid overdue request parameters: late fee = {}, overdue days = {}", request.lateFees(), request.overduedays());
            throw new InvalidRequestPayloadException("Invalid payload");

        }
        invoiceService.processOverdueInvoices(request);
        logger.info("Overdue invoices processed with late fee: {} and overdue days: {}", request.lateFees(), request.overduedays());

        resp.setData("Overdue invoices processed successfully");
        resp.setMessage(SUCCESS);
        return ResponseEntity.ok(resp); // 200 OK
    }
}
