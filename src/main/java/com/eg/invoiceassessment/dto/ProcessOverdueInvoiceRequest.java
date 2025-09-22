package com.eg.invoiceassessment.dto;

public record ProcessOverdueInvoiceRequest(Double lateFees, int overduedays) {
}
