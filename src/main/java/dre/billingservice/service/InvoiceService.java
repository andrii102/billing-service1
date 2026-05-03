package dre.billingservice.service;

import dre.billingservice.dto.payments.CreateInvoiceRequest;
import dre.billingservice.dto.payments.InvoiceDTO;
import dre.billingservice.dto.payments.UpdateInvoiceRequest;
import dre.billingservice.model.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {
    InvoiceDTO getById(Long id);

    Page<InvoiceDTO> getAll(Pageable pageable, Long userId, InvoiceStatus invoiceStatus);

    InvoiceDTO createInvoice(CreateInvoiceRequest request);

    InvoiceDTO payInvoice(Long invoiceId);

    Page<InvoiceDTO> getUserInvoices(Long userId, Pageable pageable);

    InvoiceDTO cancelInvoice(Long invoiceId);

    InvoiceDTO updateInvoice(Long invoiceId, UpdateInvoiceRequest request);

    void deleteInvoice(Long invoiceId);
}
