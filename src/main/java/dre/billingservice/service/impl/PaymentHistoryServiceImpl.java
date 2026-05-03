package dre.billingservice.service.impl;

import dre.billingservice.client.UserServiceClient;
import dre.billingservice.dto.UserDTO;
import dre.billingservice.dto.payments.CreatePaymentHistoryDTO;
import dre.billingservice.dto.payments.PaymentHistoryDTO;
import dre.billingservice.exception.EntityNotFound;
import dre.billingservice.model.Invoice;
import dre.billingservice.model.PaymentHistory;
import dre.billingservice.repository.InvoiceRepository;
import dre.billingservice.repository.PaymentHistoryRepository;
import dre.billingservice.service.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentHistoryServiceImpl implements PaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional(readOnly = true)
    public PaymentHistoryDTO getById(Long id) {
        log.info("Retrieving payment history with ID: {}", id);
        PaymentHistory paymentHistory = paymentHistoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Payment history not found with ID: {}", id);
                    return new EntityNotFound("Payment history not found");
                });
        return mapToDTO(paymentHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentHistoryDTO> getAll(Pageable pageable) {
        log.info("Retrieving all payment histories");
        return paymentHistoryRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentHistoryDTO> getHistoriesForInvoice(Long invoiceId, Pageable pageable) {
        log.info("Retrieving payment histories for invoice ID: {}", invoiceId);
        return paymentHistoryRepository.findByInvoiceId(invoiceId, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentHistoryDTO> getHistoriesForUser(Long userId, Pageable pageable) {
        log.info("Retrieving payment histories for user ID: {}", userId);
        return paymentHistoryRepository.findByUserId(userId, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentHistory> getHistoriesForInvoice(Long invoiceId) {
        log.info("Retrieving payment histories for invoice ID: {}", invoiceId);
        return paymentHistoryRepository.findByInvoiceId(invoiceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentHistory> getHistoriesForUser(Long userId) {
        log.info("Retrieving payment histories for user ID: {}", userId);
        return paymentHistoryRepository.findByUserId(userId);
    }

    @Override
    public PaymentHistoryDTO create(CreatePaymentHistoryDTO request) {
        log.info("Creating payment history for invoice ID: {}", request.invoiceId());

        Invoice invoice = invoiceRepository.findById(request.invoiceId())
                .orElseThrow(() -> new EntityNotFound("Invoice not found"));
        UserDTO user = userServiceClient.getUserById(request.userId());

        PaymentHistory paymentHistory = PaymentHistory.builder()
                .invoice(invoice)
                .userId(user.id())
                .amountPaid(request.amountPaid())
                .paymentDate(request.paymentDate() != null ? request.paymentDate() : LocalDateTime.now())
                .paymentMethod(request.paymentMethod())
                .status(request.status())
                .build();

        PaymentHistory savedPaymentHistory = paymentHistoryRepository.save(paymentHistory);
        log.info("Payment history created successfully with ID: {}", savedPaymentHistory.getId());

        return mapToDTO(savedPaymentHistory);
    }

    @Override
    public void deletePaymentHistory(Long id) {
        log.info("Deleting payment history with ID: {}", id);
        if (!paymentHistoryRepository.existsById(id)) {
            log.warn("Payment history not found with ID: {}", id);
            throw new EntityNotFound("Payment history not found");
        }
        paymentHistoryRepository.deleteById(id);
        log.info("Payment history deleted successfully with ID: {}", id);
    }

    private PaymentHistoryDTO mapToDTO(PaymentHistory paymentHistory) {
        return PaymentHistoryDTO.builder()
                .id(paymentHistory.getId())
                .invoiceId(paymentHistory.getInvoice().getId())
                .userId(paymentHistory.getUserId())
                .amountPaid(paymentHistory.getAmountPaid())
                .paymentDate(paymentHistory.getPaymentDate())
                .paymentMethod(paymentHistory.getPaymentMethod())
                .status(paymentHistory.getStatus())
                .build();
    }
}
