package dre.billingservice.service;

import dre.billingservice.dto.payments.CreatePaymentHistoryDTO;
import dre.billingservice.dto.payments.PaymentHistoryDTO;
import dre.billingservice.model.PaymentHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentHistoryService {
    PaymentHistoryDTO getById(Long id);

    Page<PaymentHistoryDTO> getAll(Pageable pageable);

    Page<PaymentHistoryDTO> getHistoriesForInvoice(Long invoiceId, Pageable pageable);

    Page<PaymentHistoryDTO> getHistoriesForUser(Long userId, Pageable pageable);

    List<PaymentHistory> getHistoriesForInvoice(Long invoiceId);

    List<PaymentHistory> getHistoriesForUser(Long userId);

    PaymentHistoryDTO create(CreatePaymentHistoryDTO createPaymentHistoryRequest);

    void deletePaymentHistory(Long id);
}
