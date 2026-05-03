package dre.billingservice.dto.payments;

import dre.billingservice.model.PaymentMethod;
import dre.billingservice.model.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentHistoryDTO(
    Long id,
    Long invoiceId,
    Long userId,
    BigDecimal amountPaid,
    LocalDateTime paymentDate,
    PaymentMethod paymentMethod,
    PaymentStatus status
) {
}
