package dre.billingservice.controller;

import dre.billingservice.dto.PaginatedResponse;
import dre.billingservice.dto.payments.PaymentHistoryDTO;
import dre.billingservice.service.PaymentHistoryService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment-history")
@RequiredArgsConstructor
public class PaymentHistoryController {
    private final PaymentHistoryService paymentHistoryService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentHistoryDTO> getById(
            @PathVariable @Positive(message = "Payment history ID must be a positive number") Long id) {
        PaymentHistoryDTO paymentHistory = paymentHistoryService.getById(id);
        return ResponseEntity.ok(paymentHistory);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<PaymentHistoryDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PaymentHistoryDTO> historyPage = paymentHistoryService.getAll(PageRequest.of(page, size));
        PaginatedResponse<PaymentHistoryDTO> response = PaginatedResponse.<PaymentHistoryDTO>builder()
                .content(historyPage.getContent())
                .pageNumber(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .last(historyPage.isLast())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentHistory(
            @PathVariable @Positive(message = "Payment history ID must be a positive number") Long id) {
        paymentHistoryService.deletePaymentHistory(id);
        return ResponseEntity.noContent().build();
    }
}
