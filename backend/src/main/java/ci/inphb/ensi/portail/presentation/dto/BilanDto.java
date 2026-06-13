package ci.inphb.ensi.portail.presentation.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Bilan simplifie : rubriques actif/passif, totaux et equilibre.
 */
public class BilanDto {

    private List<RubriqueBilanDto> actifs;
    private List<RubriqueBilanDto> passifs;
    private BigDecimal totalActif;
    private BigDecimal totalPassif;
    private boolean equilibre;

    public List<RubriqueBilanDto> getActifs() {
        return actifs;
    }

    public void setActifs(List<RubriqueBilanDto> actifs) {
        this.actifs = actifs;
    }

    public List<RubriqueBilanDto> getPassifs() {
        return passifs;
    }

    public void setPassifs(List<RubriqueBilanDto> passifs) {
        this.passifs = passifs;
    }

    public BigDecimal getTotalActif() {
        return totalActif;
    }

    public void setTotalActif(BigDecimal totalActif) {
        this.totalActif = totalActif;
    }

    public BigDecimal getTotalPassif() {
        return totalPassif;
    }

    public void setTotalPassif(BigDecimal totalPassif) {
        this.totalPassif = totalPassif;
    }

    public boolean isEquilibre() {
        return equilibre;
    }

    public void setEquilibre(boolean equilibre) {
        this.equilibre = equilibre;
    }
}
