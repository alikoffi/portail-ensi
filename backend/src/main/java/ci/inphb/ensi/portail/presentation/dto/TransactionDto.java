package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDto {

    private Long id;

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    @NotNull(message = "Le type est obligatoire")
    @Pattern(regexp = "RECETTE|DEPENSE", message = "Le type doit être RECETTE ou DEPENSE")
    private String type;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal montant;

    @NotNull(message = "La date est obligatoire")
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDateTx() {
        return dateTx;
    }

    public void setDateTx(LocalDate dateTx) {
        this.dateTx = dateTx;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
