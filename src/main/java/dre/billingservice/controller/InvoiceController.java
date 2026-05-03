package dre.billingservice.controller;

import dre.billingservice.dto.PaginatedResponse;
import dre.billingservice.dto.payments.CreateInvoiceRequest;
import dre.billingservice.dto.payments.InvoiceDTO;
import dre.billingservice.dto.payments.PaymentHistoryDTO;
import dre.billingservice.dto.payments.UpdateInvoiceRequest;
import dre.billingservice.model.InvoiceStatus;
import dre.billingservice.service.InvoiceService;
import dre.billingservice.service.PaymentHistoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final PaymentHistoryService paymentHistoryService;

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getById(
            @PathVariable @Positive(message = "Invoice ID must be a positive number") Long id) {
        InvoiceDTO invoice = invoiceService.getById(id);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<InvoiceDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) InvoiceStatus invoiceStatus,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                sortOrder.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        Page<InvoiceDTO> invoicePage = invoiceService.getAll(pageable, userId, invoiceStatus);
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

    @PostMapping
    public ResponseEntity<InvoiceDTO> create(@Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceDTO created = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<InvoiceDTO> cancelInvoice(
            @PathVariable("id") @Positive(message = "Invoice ID must be a positive number") Long invoiceId) {
        InvoiceDTO cancelledInvoice = invoiceService.cancelInvoice(invoiceId);
        return ResponseEntity.ok(cancelledInvoice);
    }

    @GetMapping("/{invoiceId}/payment-history")
    public ResponseEntity<PaginatedResponse<PaymentHistoryDTO>> getInvoicePaymentHistory(
            @PathVariable @Positive(message = "Invoice ID must be a positive number") Long invoiceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PaymentHistoryDTO> historyPage = paymentHistoryService.getHistoriesForInvoice(invoiceId, PageRequest.of(page, size));
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

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(
            @PathVariable @Positive(message = "Invoice ID must be a positive number") Long id,
            @Valid @RequestBody UpdateInvoiceRequest request) {
        InvoiceDTO updatedInvoice = invoiceService.updateInvoice(id, request);
        return ResponseEntity.ok(updatedInvoice);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<InvoiceDTO> payInvoice(
            @PathVariable @Positive(message = "Invoice ID must be a positive number") Long id) {
        InvoiceDTO paidInvoice = invoiceService.payInvoice(id);
        return ResponseEntity.ok(paidInvoice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(
            @PathVariable @Positive(message = "Invoice ID must be a positive number") Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
