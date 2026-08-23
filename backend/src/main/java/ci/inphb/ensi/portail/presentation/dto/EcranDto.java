package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Ecran;

public class EcranDto {

    private Long id;
    private String code;
    private String libelle;
    private boolean visible;

    /** Un ecran verrouille est toujours visible : l'interrupteur reste desactive. */
    private boolean verrouille;

    private int ordre;

    public EcranDto() {
    }

    public EcranDto(Ecran ecran) {
        this.id = ecran.getId();
        this.code = ecran.getCode();
        this.libelle = ecran.getLibelle();
        this.visible = ecran.isVisible();
        this.verrouille = ecran.isVerrouille();
        this.ordre = ecran.getOrdre();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVerrouille() {
        return verrouille;
    }

    public void setVerrouille(boolean verrouille) {
        this.verrouille = verrouille;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }
}
