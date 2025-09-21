package com.eg.invoiceassessment.entity;

import com.eg.invoiceassessment.listener.AuditListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditListener.class)
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be a positive value")
    private Double amount;

    @NotNull(message = "Paid amount cannot be null")
    @PositiveOrZero(message = "Paid amount must be zero or positive")
    private Double paidAmount = 0.0;

    @NotNull(message = "Due date cannot be null")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status cannot be null")
    private Status status;

    @NotNull(message = "Created On must be today or in the future")
    private LocalDateTime createdAt;

    @NotNull(message = "Updated At must be today or in the future")
    private LocalDateTime updatedAt;


    private LocalDateTime paidOn;

    public enum Status {
        PENDING, PAID, VOID
    }


    public Invoice(Double amount, LocalDate dueDate) {
        this.amount=amount;
        this.dueDate=dueDate;
    }


}

