package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Membre;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ligne de recouvrement : un membre et le cumul de ses cotisations.
 */
public class MembreRecouvrementDto {

    private Long id;
    private String nom;
    private String prenoms;
    private String statut;
    private BigDecimal totalCotise;
    private long nombrePaiements;
    private LocalDate dernierPaiement;

    public MembreRecouvrementDto(Membre membre, BigDecimal totalCotise, long nombrePaiements, LocalDate dernierPaiement) {
        this.id = membre.getId();
        this.nom = membre.getNom();
        this.prenoms = membre.getPrenoms();
        this.statut = membre.getStatut().name();
        this.totalCotise = totalCotise;
        this.nombrePaiements = nombrePaiements;
        this.dernierPaiement = dernierPaiement;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenoms() {
        return prenoms;
    }

    public String getStatut() {
        return statut;
    }

    public BigDecimal getTotalCotise() {
        return totalCotise;
    }

    public long getNombrePaiements() {
        return nombrePaiements;
    }

    public LocalDate getDernierPaiement() {
        return dernierPaiement;
    }
}
