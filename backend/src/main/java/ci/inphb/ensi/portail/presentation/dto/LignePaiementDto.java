package ci.inphb.ensi.portail.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ligne du detail d'un appel : un membre, ce qu'il a paye et ce qu'il reste.
 */
public class LignePaiementDto {

    private Long paiementId;
    private Long membreId;
    private String membreNom;
    private String statutMembre;
    private BigDecimal montantAttendu;
    private BigDecimal montantPaye;
    private BigDecimal reste;
    private boolean aJour;
    private LocalDate datePaiement;
    private String note;

    public Long getPaiementId() {
        return paiementId;
    }

    public void setPaiementId(Long paiementId) {
        this.paiementId = paiementId;
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

    public String getStatutMembre() {
        return statutMembre;
    }

    public void setStatutMembre(String statutMembre) {
        this.statutMembre = statutMembre;
    }

    public BigDecimal getMontantAttendu() {
        return montantAttendu;
    }

    public void setMontantAttendu(BigDecimal montantAttendu) {
        this.montantAttendu = montantAttendu;
    }

    public BigDecimal getMontantPaye() {
        return montantPaye;
    }

    public void setMontantPaye(BigDecimal montantPaye) {
        this.montantPaye = montantPaye;
    }

    public BigDecimal getReste() {
        return reste;
    }

    public void setReste(BigDecimal reste) {
        this.reste = reste;
    }

    public boolean isAJour() {
        return aJour;
    }

    public void setAJour(boolean aJour) {
        this.aJour = aJour;
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
