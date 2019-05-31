package es.upm.miw.rest_controllers;

import es.upm.miw.business_controllers.InvoiceUpdateController;
import es.upm.miw.dtos.input.TicketQueryInputDto;
import es.upm.miw.dtos.output.InvoiceUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@PreAuthorize("hasRole ('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(InvoiceUpdateResource.INVOICEUPDATE)
public class InvoiceUpdateResource {
    public static final String MOBILEID = "/mobile/{mobile}";
    public static final String BETWEENDATES = "/dates/{afterDate}/{beforeDate}";
    public static final String MOBILEIDBETWEENDATES = "/mobiledates/{mobile}/{afterDate}/{beforeDate}";
    public static final String INVOICEUPDATE = "/invoice-update";
    public static final String PDF = "/pdf/{id}";
    public static final String MAXNEGATIVE = "/maxnegative/{id}";
    public static final String NEGATIVE = "/negative";
    @Autowired
    private InvoiceUpdateController invoiceUpdateController;

    @GetMapping()
    public List<InvoiceUpdateDto> getAll() {
        return invoiceUpdateController.getAll();
    }

    @GetMapping(value = MOBILEID)
    public List<InvoiceUpdateDto> getInvoicesByMobile(@PathVariable String mobile) {
        return invoiceUpdateController.getInvoiceByMobile(mobile);
    }
    @RequestMapping(value = PDF, produces = {"application/pdf"}, method=GET)
    public byte[] getInvoicePDF(@PathVariable String id){
        byte[] response = invoiceUpdateController.generatePdf(id);
        return response;
    }
    @GetMapping(value = BETWEENDATES)
    public List<InvoiceUpdateDto> getInvoicesByCreationDateBetween(@PathVariable String afterDate, @PathVariable String beforeDate) {
            return invoiceUpdateController.getInvoiceByCreationDateBetween(afterDate, beforeDate);
    }
    @GetMapping(value = MOBILEIDBETWEENDATES)
    public List<InvoiceUpdateDto> getInvoicesByMobileAndCreationDateBetween(@PathVariable String mobile,
                                                                   @PathVariable String afterDate,
                                                                   @PathVariable String beforeDate) {
        return invoiceUpdateController.getInvoiceByMobileAndCreationDateBetween(mobile,
                                                                                afterDate,
                                                                                beforeDate);
    }
    @GetMapping(value = MAXNEGATIVE)
    public BigDecimal look4PosibleTotal (@PathVariable String id) {
        return invoiceUpdateController.look4PosibleTotal(id);
    }
    @PostMapping(value = NEGATIVE, produces = {"application/pdf", "application/json"})
    public byte[] createNegativeInvoiceAndPdf (@RequestBody InvoiceUpdateDto invoiceUpdateDto){
        return invoiceUpdateController.createNegativeInvoiceAndPdf(invoiceUpdateDto);
    }

}
