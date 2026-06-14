package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Pv;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class PvDto {

    private Long id;

    @NotBlank(message = "L'objet est obligatoire")
    private String objet;

    @NotNull(message = "La date est obligatoire")
    private LocalDate datePv;

    private String lieu;
    private String presents;
    private String ordreDuJour;
    private String decisions;
    private String signataires;

    public PvDto() {
    }

    public PvDto(Pv pv) {
        this.id = pv.getId();
        this.objet = pv.getObjet();
        this.datePv = pv.getDatePv();
        this.lieu = pv.getLieu();
        this.presents = pv.getPresents();
        this.ordreDuJour = pv.getOrdreDuJour();
        this.decisions = pv.getDecisions();
        this.signataires = pv.getSignataires();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public LocalDate getDatePv() {
        return datePv;
    }

    public void setDatePv(LocalDate datePv) {
        this.datePv = datePv;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getPresents() {
        return presents;
    }

    public void setPresents(String presents) {
        this.presents = presents;
    }

    public String getOrdreDuJour() {
        return ordreDuJour;
    }

    public void setOrdreDuJour(String ordreDuJour) {
        this.ordreDuJour = ordreDuJour;
    }

    public String getDecisions() {
        return decisions;
    }

    public void setDecisions(String decisions) {
        this.decisions = decisions;
    }

    public String getSignataires() {
        return signataires;
    }

    public void setSignataires(String signataires) {
        this.signataires = signataires;
    }
}
