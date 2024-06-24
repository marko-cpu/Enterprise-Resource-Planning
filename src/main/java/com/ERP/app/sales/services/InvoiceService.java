package com.ERP.app.sales.services;

import com.ERP.app.sales.data.Invoice;
import com.ERP.app.sales.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Invoice createInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Long id, Invoice updatedInvoice) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(id);
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            invoice.setAccounting(updatedInvoice.getAccounting());
            invoice.setTotalPrice(updatedInvoice.getTotalPrice());
            invoice.setPayDate(updatedInvoice.getPayDate());
            return invoiceRepository.save(invoice);
        }
        return null; // Or throw an exception
    }

    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

    public List<Invoice> findInvoicesByPayDate(LocalDate payDate) {
        return invoiceRepository.findByPayDate(payDate);
    }
}
