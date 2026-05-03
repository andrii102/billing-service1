package dre.billingservice.repository;

import dre.billingservice.model.Invoice;
import dre.billingservice.model.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Page<Invoice> findByUserId(Long userId, Pageable pageable);
    List<Invoice> findByMembershipId(Long membershipId);

    @Query("SELECT i FROM Invoice i WHERE " +
            "(:userId IS NULL OR i.userId = :userId) AND " +
            "(:invoiceStatus IS NULL OR i.status = :invoiceStatus)")
    Page<Invoice> findInvoicesByUserIdAndStatus(Long userId, InvoiceStatus invoiceStatus, Pageable pageable);
}
