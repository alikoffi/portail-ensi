package ci.inphb.ensi.portail.presentation.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Synthese du recouvrement des cotisations.
 */
public class RecouvrementDto {

    private List<MembreRecouvrementDto> membres;
    private BigDecimal totalGeneral;
    private long nombreMembres;
    private long nombreMembresActifs;

    public List<MembreRecouvrementDto> getMembres() {
        return membres;
    }

    public void setMembres(List<MembreRecouvrementDto> membres) {
        this.membres = membres;
    }

    public BigDecimal getTotalGeneral() {
        return totalGeneral;
    }

    public void setTotalGeneral(BigDecimal totalGeneral) {
        this.totalGeneral = totalGeneral;
    }

    public long getNombreMembres() {
        return nombreMembres;
    }

    public void setNombreMembres(long nombreMembres) {
        this.nombreMembres = nombreMembres;
    }

    public long getNombreMembresActifs() {
        return nombreMembresActifs;
    }

    public void setNombreMembresActifs(long nombreMembresActifs) {
        this.nombreMembresActifs = nombreMembresActifs;
    }
}
