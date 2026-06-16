package ci.inphb.ensi.portail.presentation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Saisie du montant paye par un membre pour un appel.
 */
public class PaiementDto {

    @NotNull(message = "L'appel est obligatoire")
    private Long appelId;

    @NotNull(message = "Le membre est obligatoire")
    private Long membreId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal montant;

    private LocalDate datePaiement;
    private String note;

    public Long getAppelId() {
        return appelId;
    }

    public void setAppelId(Long appelId) {
        this.appelId = appelId;
    }

    public Long getMembreId() {
        return membreId;
    }

    public void setMembreId(Long membreId) {
        this.membreId = membreId;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
