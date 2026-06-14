package ci.inphb.ensi.portail.presentation.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Donnees pretes a tracer pour les graphiques du tableau de bord (tableaux paralleles).
 */
public class StatistiqueDto {

    /** Libelles des mois (ex. "janv. 2026"). */
    private List<String> moisLabels;

    /** Solde cumule a la fin de chaque mois. */
    private List<BigDecimal> soldeCumule;

    /** Categories de depenses. */
    private List<String> categoriesDepenses;

    /** Montant total de depenses par categorie. */
    private List<BigDecimal> montantsDepenses;

    public List<String> getMoisLabels() {
        return moisLabels;
    }

    public void setMoisLabels(List<String> moisLabels) {
        this.moisLabels = moisLabels;
    }

    public List<BigDecimal> getSoldeCumule() {
        return soldeCumule;
    }

    public void setSoldeCumule(List<BigDecimal> soldeCumule) {
        this.soldeCumule = soldeCumule;
    }

    public List<String> getCategoriesDepenses() {
        return categoriesDepenses;
    }

    public void setCategoriesDepenses(List<String> categoriesDepenses) {
        this.categoriesDepenses = categoriesDepenses;
    }

    public List<BigDecimal> getMontantsDepenses() {
        return montantsDepenses;
    }

    public void setMontantsDepenses(List<BigDecimal> montantsDepenses) {
        this.montantsDepenses = montantsDepenses;
    }
}
