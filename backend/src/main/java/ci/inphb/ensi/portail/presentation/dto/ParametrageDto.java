package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Parametrage;
import jakarta.validation.constraints.NotBlank;

public class ParametrageDto {

    private Long id;

    @NotBlank(message = "La catégorie est obligatoire")
    private String categorie;

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    private int ordre;
    private boolean actif = true;

    public ParametrageDto() {
    }

    public ParametrageDto(Parametrage p) {
        this.id = p.getId();
        this.categorie = p.getCategorie();
        this.libelle = p.getLibelle();
        this.ordre = p.getOrdre();
        this.actif = p.isActif();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
