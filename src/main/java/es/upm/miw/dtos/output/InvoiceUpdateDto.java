package es.upm.miw.dtos.output;

import java.math.BigDecimal;

public class InvoiceUpdateDto {
    String id;
    String creationDate;
    BigDecimal baseTax;
    BigDecimal tax;
    String referencespositiveinvoice;

    public InvoiceUpdateDto(String id, String creationDate, BigDecimal baseTax, BigDecimal tax, String referencespositiveinvoice) {
        this.id = id;
        this.creationDate = creationDate;
        this.baseTax = baseTax;
        this.tax = tax;
        this.referencespositiveinvoice =referencespositiveinvoice;
    }

    public String getReferencespositiveinvoice() {
        return referencespositiveinvoice;
    }

    public void setReferencespositiveinvoice(String referencespositiveinvoice) {
        this.referencespositiveinvoice = referencespositiveinvoice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public BigDecimal getBaseTax() {
        return baseTax;
    }

    public void setBaseTax(BigDecimal baseTax) {
        this.baseTax = baseTax;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }
}
