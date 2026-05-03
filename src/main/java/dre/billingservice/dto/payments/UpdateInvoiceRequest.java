package dre.billingservice.dto.payments;

import dre.billingservice.model.InvoiceStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateInvoiceRequest (
        @Positive(message = "Membership ID must be a positive number")
        Long membershipId,

        @Positive(message = "User ID must be a positive number")
        Long userId,

        @DecimalMin(value = "0", message = "Sub total must be greater than or equal to 0")
        BigDecimal subTotal,

        @DecimalMin(value = "0", message = "Total amount must be greater than or equal to 0")
        BigDecimal totalAmount,

        LocalDateTime dueDate,
        InvoiceStatus status,
        LocalDateTime paidDate,
        String description,

        @DecimalMin(value = "0", message = "Discount must be greater than or equal to 0")
        BigDecimal discount
){
}
