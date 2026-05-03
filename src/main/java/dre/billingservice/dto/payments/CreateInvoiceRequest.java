package dre.billingservice.dto.payments;

import dre.billingservice.model.InvoiceStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateInvoiceRequest(
    @NotNull(message = "Membership ID cannot be null")
    @Positive(message = "Membership ID must be a positive number")
    Long membershipId,

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be a positive number")
    Long userId,

    @NotNull(message = "Sub total cannot be null")
    @DecimalMin(value = "0", message = "Sub total must be greater than or equal to 0")
    BigDecimal subTotal,

    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0", message = "Total amount must be greater than or equal to 0")
    BigDecimal totalAmount,

    @NotNull(message = "Due date cannot be null")
    LocalDateTime dueDate,

    @NotNull(message = "Status cannot be null")
    InvoiceStatus status,

    LocalDateTime paidDate,

    String description,

    @DecimalMin(value = "0", message = "Discount must be greater than or equal to 0")
    BigDecimal discount
) {}
