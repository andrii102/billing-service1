package dre.billingservice.dto.payments;

import dre.billingservice.model.PaymentMethod;
import dre.billingservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePaymentHistoryDTO(
    Long invoiceId,
    Long userId,
    BigDecimal amountPaid,
    LocalDateTime paymentDate,
    PaymentMethod paymentMethod,
    PaymentStatus status
) {}
