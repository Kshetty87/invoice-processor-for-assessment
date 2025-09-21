package com.eg.invoiceassessment.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;


public record ErrorResponse (String message,  int status, Map<String, String> validationErrors, String path, LocalDateTime timestamp){

}
