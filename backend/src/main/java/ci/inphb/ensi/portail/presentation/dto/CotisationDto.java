package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Cotisation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CotisationDto {

    private Long id;

    @NotNull(message = "Le membre est obligatoire")
    private Long membreId;

    private String membreNom;

    @NotBlank(message = "La période est obligatoire")
    private String periode;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal montant;

    @NotNull(message = "La date de paiement est obligatoire")
    private LocalDate datePaiement;

    private String note;

    public CotisationDto() {
    }

    public CotisationDto(Cotisation c) {
        this.id = c.getId();
        this.membreId = c.getMembre().getId();
        this.membreNom = c.getMembre().getNom()
                + (c.getMembre().getPrenoms() != null ? " " + c.getMembre().getPrenoms() : "");
        this.periode = c.getPeriode();
        this.montant = c.getMontant();
        this.datePaiement = c.getDatePaiement();
        this.note = c.getNote();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMembreId() {
        return membreId;
    }

    public void setMembreId(Long membreId) {
        this.membreId = membreId;
    }

    public String getMembreNom() {
        return membreNom;
    }

    public void setMembreNom(String membreNom) {
        this.membreNom = membreNom;
    }

    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
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
