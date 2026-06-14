package ci.inphb.ensi.portail.presentation.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Evolution des cotisations par mois pour une annee donnee.
 */
public class StatistiqueCotisationDto {

    private int annee;
    private List<Integer> anneesDisponibles;
    private List<String> moisLabels;
    private List<BigDecimal> montantsParMois;
    private BigDecimal totalAnnee;

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public List<Integer> getAnneesDisponibles() {
        return anneesDisponibles;
    }

    public void setAnneesDisponibles(List<Integer> anneesDisponibles) {
        this.anneesDisponibles = anneesDisponibles;
    }

    public List<String> getMoisLabels() {
        return moisLabels;
    }

    public void setMoisLabels(List<String> moisLabels) {
        this.moisLabels = moisLabels;
    }

    public List<BigDecimal> getMontantsParMois() {
        return montantsParMois;
    }

    public void setMontantsParMois(List<BigDecimal> montantsParMois) {
        this.montantsParMois = montantsParMois;
    }

    public BigDecimal getTotalAnnee() {
        return totalAnnee;
    }

    public void setTotalAnnee(BigDecimal totalAnnee) {
        this.totalAnnee = totalAnnee;
    }
}
