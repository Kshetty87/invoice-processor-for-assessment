package com.eg.invoiceassessment.listener;

import com.eg.invoiceassessment.entity.Invoice;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;


public class AuditListener {

    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof Invoice invoice) {
            invoice.setPaidAmount(0.0);
            invoice.setStatus(Invoice.Status.PENDING);
            invoice.setCreatedAt(LocalDateTime.now());
            invoice.setUpdatedAt(LocalDateTime.now());

        }
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        if (entity instanceof Invoice invoice) {
            invoice.setUpdatedAt(LocalDateTime.now());
            if (invoice.getStatus().name().equalsIgnoreCase(Invoice.Status.PAID.name()) && invoice.getPaidOn()==null) {
                invoice.setPaidOn(LocalDateTime.now());
            }
        }
    }
}
