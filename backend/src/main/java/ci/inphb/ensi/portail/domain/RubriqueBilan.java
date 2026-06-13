package ci.inphb.ensi.portail.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.math.BigDecimal;

/**
 * Champs communs aux rubriques de bilan (actif et passif).
 */
@MappedSuperclass
public abstract class RubriqueBilan extends AbstractEntity {

    @Column(name = "rubrique", nullable = false)
    private String rubrique;

    @Column(name = "montant", nullable = false)
    private BigDecimal montant;

    public void mettreAJour(String rubrique, BigDecimal montant) {
        this.rubrique = rubrique;
        this.montant = montant;
    }

    public String getRubrique() {
        return rubrique;
    }

    public BigDecimal getMontant() {
        return montant;
    }
}
