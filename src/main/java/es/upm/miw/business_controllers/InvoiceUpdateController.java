package es.upm.miw.business_controllers;

import es.upm.miw.business_services.PdfService;
import es.upm.miw.documents.Invoice;
import es.upm.miw.documents.Ticket;
import es.upm.miw.documents.User;
import es.upm.miw.dtos.output.InvoiceUpdateDto;
import es.upm.miw.repositories.InvoiceRepository;
import es.upm.miw.repositories.TicketRepository;
import es.upm.miw.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Controller
public class InvoiceUpdateController {
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private PdfService pdfService;

    private List<InvoiceUpdateDto> convertInvoiceToInvoiceUpdateDto(List<Invoice> invoices) {
        InvoiceUpdateDto invoiceUpdateDto;
        List<InvoiceUpdateDto> invoiceUpdateDtoList = new ArrayList<InvoiceUpdateDto>();
        for (Invoice invoice : invoices) {
            invoiceUpdateDto = new InvoiceUpdateDto(
                    invoice.getId(),
                    invoice.getCreationDate().toString(),
                    Float.parseFloat(invoice.getBaseTax().toString()),
                    Float.parseFloat(invoice.getTax().toString())
            );
            invoiceUpdateDtoList.add(invoiceUpdateDto);
        }
        return invoiceUpdateDtoList;
    }
    private LocalDateTime convertStringToLocalDateTime(String date) {
        System.out.println("Fecha: "+date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date convertedDate = null;
        try{
            convertedDate = simpleDateFormat.parse(date);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return convertedDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

    }
    public List<InvoiceUpdateDto> getAll() {
        return convertInvoiceToInvoiceUpdateDto(this.invoiceRepository.findAll());
    }
    public List<InvoiceUpdateDto> getInvoiceByMobile(String mobile) {
        Optional<User> userOptional = userRepository.findByMobile(mobile);
        User user = userOptional.get();
        List<Invoice> invoices = invoiceRepository.findByUser(user);
        return convertInvoiceToInvoiceUpdateDto(invoices);
    }
    public List<InvoiceUpdateDto> getInvoiceByCreationDateAfter(String afterDate) {
        List<Invoice> invoices = invoiceRepository
                .findByCreationDateAfter(convertStringToLocalDateTime(afterDate));
        return convertInvoiceToInvoiceUpdateDto(invoices);
    }
    public List<InvoiceUpdateDto> getInvoiceByCreationDateBetween(String afterDate, String beforeDate) {
        List<Invoice> invoices = invoiceRepository
                .findByCreationDateBetween(convertStringToLocalDateTime(afterDate),
                                            convertStringToLocalDateTime(beforeDate));
        return convertInvoiceToInvoiceUpdateDto(invoices);
    }
    public List<InvoiceUpdateDto> getInvoiceByMobileAndCreationDateBetween(String mobile,
                                                                           String afterDate,
                                                                           String beforeDate) {
        Optional<User> userOptional = userRepository.findByMobile(mobile);
        User user = userOptional.get();
        List<Invoice> invoices = invoiceRepository
                .findByUserAndCreationDateBetween(user, convertStringToLocalDateTime(afterDate),
                        convertStringToLocalDateTime(beforeDate));
        return convertInvoiceToInvoiceUpdateDto(invoices);
    }
    public byte[] generatePdf(String id) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        Optional<Ticket> ticket = ticketRepository.findById(invoice.get().getTicket().getId());
        return this.pdfService.generateInvoice(invoice.get(), ticket.get());
    }
    public BigDecimal look4PosibleTotal (String id) {
        BigDecimal posibleTotal = new BigDecimal(0);
        Optional<List<Invoice>> negativeinvoices = null;
        Optional<Invoice> positiveinvoice = invoiceRepository.findById(id);

        if (positiveinvoice.isPresent()){
            posibleTotal = new BigDecimal(String.valueOf(positiveinvoice.get().getTicket().getCash()));
            System.out.println("positive Cash: " + posibleTotal.toString());
            posibleTotal = posibleTotal.add(positiveinvoice.get().getTicket().getCard());
            System.out.println("positive Card+Cash: " + posibleTotal.toString());
            negativeinvoices = Optional.ofNullable(invoiceRepository.findByReferencespositiveinvoice(positiveinvoice.get()));
            System.out.println("negativeinvoices " + negativeinvoices.get().toString());
        }
        if (negativeinvoices.isPresent()) {
            BigDecimal cash;
            BigDecimal card;
            for (Invoice negativeinvoice : negativeinvoices.get()) {
                cash = negativeinvoice.getTicket().getCard();
                card = negativeinvoice.getTicket().getCash();
                posibleTotal = posibleTotal.subtract(cash);
                posibleTotal = posibleTotal.subtract(card);
            }
        }
        return posibleTotal;
    }
}
