package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDto {

    private Long id;
    private String libelle;
    private String type;
    private BigDecimal montant;
    private LocalDate dateTx;
    private String categorie;
    private String note;

    public TransactionDto() {
    }

    public TransactionDto(Transaction t) {
        this.id = t.getId();
        this.libelle = t.getLibelle();
        this.type = t.getType().name();
        this.montant = t.getMontant();
        this.dateTx = t.getDateTx();
        this.categorie = t.getCategorie();
        this.note = t.getNote();
    }

    public Long getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public LocalDate getDateTx() {
        return dateTx;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getNote() {
        return note;
    }
}
