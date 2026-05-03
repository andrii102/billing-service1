package dre.billingservice.controller;


import dre.billingservice.dto.PaginatedResponse;
import dre.billingservice.dto.payments.InvoiceDTO;
import dre.billingservice.dto.payments.PaymentHistoryDTO;
import dre.billingservice.service.InvoiceService;
import dre.billingservice.service.PaymentHistoryService;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final InvoiceService invoiceService;
    private final PaymentHistoryService paymentHistoryService;

    @GetMapping("/{userId}/invoices")
    public ResponseEntity<PaginatedResponse<InvoiceDTO>> getUserInvoices(
            @PathVariable @Positive(message = "User ID must be a positive number") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<InvoiceDTO> invoicePage = invoiceService.getUserInvoices(userId, PageRequest.of(page, size));
        PaginatedResponse<InvoiceDTO> response = PaginatedResponse.<InvoiceDTO>builder()
                .content(invoicePage.getContent())
                .pageNumber(invoicePage.getNumber())
                .pageSize(invoicePage.getSize())
                .totalElements(invoicePage.getTotalElements())
                .totalPages(invoicePage.getTotalPages())
                .last(invoicePage.isLast())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/payment-history")
    public ResponseEntity<PaginatedResponse<PaymentHistoryDTO>> getUserPaymentHistory(
            @PathVariable @Positive(message = "User ID must be a positive number") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PaymentHistoryDTO> historyPage = paymentHistoryService.getHistoriesForUser(userId, PageRequest.of(page, size));
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

}
