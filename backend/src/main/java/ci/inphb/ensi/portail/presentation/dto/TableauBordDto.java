package ci.inphb.ensi.portail.presentation.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Synthese affichee sur le tableau de bord.
 */
public class TableauBordDto {

    private BigDecimal solde;
    private BigDecimal recettesTotales;
    private BigDecimal depensesTotales;
    private long evenementsCeMois;
    private List<EvenementDto> prochainsEvenements;
    private List<EvenementDto> derniersEvenements;
    private List<TransactionDto> dernieresTransactions;

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    public BigDecimal getRecettesTotales() {
        return recettesTotales;
    }

    public void setRecettesTotales(BigDecimal recettesTotales) {
        this.recettesTotales = recettesTotales;
    }

    public BigDecimal getDepensesTotales() {
        return depensesTotales;
    }

    public void setDepensesTotales(BigDecimal depensesTotales) {
        this.depensesTotales = depensesTotales;
    }

    public long getEvenementsCeMois() {
        return evenementsCeMois;
    }

    public void setEvenementsCeMois(long evenementsCeMois) {
        this.evenementsCeMois = evenementsCeMois;
    }

    public List<EvenementDto> getProchainsEvenements() {
        return prochainsEvenements;
    }

    public void setProchainsEvenements(List<EvenementDto> prochainsEvenements) {
        this.prochainsEvenements = prochainsEvenements;
    }

    public List<EvenementDto> getDerniersEvenements() {
        return derniersEvenements;
    }

    public void setDerniersEvenements(List<EvenementDto> derniersEvenements) {
        this.derniersEvenements = derniersEvenements;
    }

    public List<TransactionDto> getDernieresTransactions() {
        return dernieresTransactions;
    }

    public void setDernieresTransactions(List<TransactionDto> dernieresTransactions) {
        this.dernieresTransactions = dernieresTransactions;
    }
}
