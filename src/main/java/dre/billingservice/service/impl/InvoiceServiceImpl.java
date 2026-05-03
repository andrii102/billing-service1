package dre.billingservice.service.impl;

import dre.billingservice.client.MembershipServiceClient;
import dre.billingservice.client.UserServiceClient;
import dre.billingservice.dto.MembershipDTO;
import dre.billingservice.dto.UserDTO;
import dre.billingservice.dto.payments.CreateInvoiceRequest;
import dre.billingservice.dto.payments.CreatePaymentHistoryDTO;
import dre.billingservice.dto.payments.InvoiceDTO;
import dre.billingservice.dto.payments.UpdateInvoiceRequest;
import dre.billingservice.exception.EntityNotFound;
import dre.billingservice.exception.InvoiceAlreadyPaidException;
import dre.billingservice.exception.InvoiceCanceledExceptinon;
import dre.billingservice.model.Invoice;
import dre.billingservice.model.InvoiceStatus;
import dre.billingservice.model.PaymentMethod;
import dre.billingservice.model.PaymentStatus;
import dre.billingservice.repository.InvoiceRepository;
import dre.billingservice.service.InvoiceService;
import dre.billingservice.service.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentHistoryService paymentHistoryService;
    private final MembershipServiceClient membershipServiceClient;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO getById(Long id) {
        log.info("Retrieving invoice with ID: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Invoice not found with ID: {}", id);
                    return new EntityNotFound("Invoice not found");
                });
        return toDTO(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getAll(Pageable pageable, Long userId, InvoiceStatus invoiceStatus) {
        log.info("Retrieving all invoices");

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        return invoiceRepository.findInvoicesByUserIdAndStatus(userId, invoiceStatus, pageable)
                .map(this::toDTO);
    }

    @Override
    public InvoiceDTO createInvoice(CreateInvoiceRequest request) {
        log.info("Creating new invoice for membership ID: {}", request.membershipId());

        UserDTO user = userServiceClient.getUserById(request.userId());

        Invoice invoice = Invoice.builder()
                .userId(user.id())
                .membershipId(request.membershipId())
                .discount(request.discount())
                .subTotal(request.subTotal())
                .totalAmount(request.subTotal().subtract(request.discount()))
                .dueDate(request.dueDate())
                .status(request.status())
                .createdDate(LocalDateTime.now())
                .paidDate(request.paidDate())
                .description(request.description())
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created successfully with ID: {}", savedInvoice.getId());
        return toDTO(savedInvoice);
    }

    @Override
    public InvoiceDTO payInvoice(Long invoiceId) {
        log.info("Processing payment for invoice ID: {}", invoiceId);
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFound("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new InvoiceAlreadyPaidException("Invoice is already paid.");
        }
        if (invoice.getStatus() == InvoiceStatus.CANCELED) {
            throw new InvoiceCanceledExceptinon("Cannot pay a cancelled invoice.");
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidDate(LocalDateTime.now());
        Invoice savedInvoice = invoiceRepository.save(invoice);

        paymentHistoryService.create(new CreatePaymentHistoryDTO(
                invoice.getId(),
                invoice.getUserId(),
                invoice.getTotalAmount(),
                invoice.getPaidDate(),
                PaymentMethod.CASH,   // To be filled by the payment gateway response
                PaymentStatus.COMPLETED    // To be filled by the payment gateway response
        ));

        log.info("Payment processed successfully for invoice ID: {}", invoiceId);
        return toDTO(savedInvoice);
    }

    @Override
    public InvoiceDTO cancelInvoice(Long invoiceId) {
        log.info("Cancelling invoice ID: {}", invoiceId);
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFound("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Cannot cancel an invoice that has already been paid.");
        }

        invoice.setStatus(InvoiceStatus.CANCELED);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice cancelled successfully");
        return toDTO(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getUserInvoices(Long userId, Pageable pageable) {
        log.info("Retrieving invoices for user ID: {}", userId);
        return invoiceRepository.findByUserId(userId, pageable)
                .map(this::toDTO);
    }

    @Override
    public InvoiceDTO updateInvoice(Long invoiceId, UpdateInvoiceRequest request) {
        log.info("Updating invoice with ID: {}", invoiceId);
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFound("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELED) {
            throw new IllegalStateException("Cannot update a paid or cancelled invoice");
        }

        MembershipDTO membership = membershipServiceClient.getMembershipById(request.membershipId());
        UserDTO user = userServiceClient.getUserById(request.userId());

        invoice.setMembershipId(membership.id());
        invoice.setUserId(user.id());
        invoice.setDiscount(request.discount());
        invoice.setSubTotal(request.subTotal());
        invoice.setTotalAmount(request.subTotal().subtract(request.discount()));
        invoice.setDueDate(request.dueDate());
        invoice.setDescription(request.description());

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice updated successfully with ID: {}", invoiceId);
        return toDTO(updatedInvoice);
    }

    @Override
    public void deleteInvoice(Long invoiceId) {
        log.info("Deleting invoice with ID: {}", invoiceId);
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFound("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Cannot delete a paid invoice");
        }

        invoiceRepository.deleteById(invoiceId);
        log.info("Invoice deleted successfully with ID: {}", invoiceId);
    }

    private InvoiceDTO toDTO(Invoice invoice) {
        return InvoiceDTO.builder()
                .id(invoice.getId())
                .membershipId(invoice.getMembershipId())
                .userId(invoice.getUserId())
                .subTotal(invoice.getSubTotal())
                .totalAmount(invoice.getTotalAmount())
                .dueDate(invoice.getDueDate())
                .status(invoice.getStatus())
                .createdDate(invoice.getCreatedDate())
                .paidDate(invoice.getPaidDate())
                .description(invoice.getDescription())
                .discount(invoice.getDiscount())
                .build();
    }
}
