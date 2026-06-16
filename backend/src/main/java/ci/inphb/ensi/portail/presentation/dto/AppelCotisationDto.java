package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.AppelCotisation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AppelCotisationDto {

    private Long id;

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    @NotNull(message = "Le montant attendu est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal montantAttendu;

    @NotNull(message = "La date butoir est obligatoire")
    private LocalDate dateButoir;

    private LocalDate dateCreation;
    private boolean cloture;

    // Agregats (lecture)
    private BigDecimal totalCollecte;
    private long nombrePayeurs;
    private boolean echeanceDepassee;

    public AppelCotisationDto() {
    }

    public AppelCotisationDto(AppelCotisation a) {
        this.id = a.getId();
        this.libelle = a.getLibelle();
        this.montantAttendu = a.getMontantAttendu();
        this.dateButoir = a.getDateButoir();
        this.dateCreation = a.getDateCreation();
        this.cloture = a.isCloture();
    }

    public AppelCotisationDto(AppelCotisation a, BigDecimal totalCollecte, long nombrePayeurs) {
        this(a);
        this.totalCollecte = totalCollecte;
        this.nombrePayeurs = nombrePayeurs;
        this.echeanceDepassee = a.getDateButoir() != null && a.getDateButoir().isBefore(LocalDate.now());
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

    public BigDecimal getMontantAttendu() {
        return montantAttendu;
    }

    public void setMontantAttendu(BigDecimal montantAttendu) {
        this.montantAttendu = montantAttendu;
    }

    public LocalDate getDateButoir() {
        return dateButoir;
    }

    public void setDateButoir(LocalDate dateButoir) {
        this.dateButoir = dateButoir;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public boolean isCloture() {
        return cloture;
    }

    public BigDecimal getTotalCollecte() {
        return totalCollecte;
    }

    public long getNombrePayeurs() {
        return nombrePayeurs;
    }

    public boolean isEcheanceDepassee() {
        return echeanceDepassee;
    }
}
