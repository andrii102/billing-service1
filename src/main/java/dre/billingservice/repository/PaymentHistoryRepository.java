package dre.billingservice.repository;

import dre.billingservice.model.PaymentHistory;
import dre.billingservice.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    Page<PaymentHistory> findByUserId(Long userId, Pageable pageable);
    Page<PaymentHistory> findByInvoiceId(Long invoiceId, Pageable pageable);
    List<PaymentHistory> findByUserId(Long userId);
    List<PaymentHistory> findByInvoiceId(Long invoiceId);
    List<PaymentHistory> findByStatus(PaymentStatus status);
}
