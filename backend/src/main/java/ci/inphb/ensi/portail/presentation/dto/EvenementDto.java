package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Evenement;

import java.time.LocalDate;

public class EvenementDto {

    private Long id;
    private String nom;
    private LocalDate dateEvent;
    private String heure;
    private String type;
    private String lieu;
    private String description;

    public EvenementDto() {
    }

    public EvenementDto(Evenement e) {
        this.id = e.getId();
        this.nom = e.getNom();
        this.dateEvent = e.getDateEvent();
        this.heure = e.getHeure();
        this.type = e.getType();
        this.lieu = e.getLieu();
        this.description = e.getDescription();
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public LocalDate getDateEvent() {
        return dateEvent;
    }

    public String getHeure() {
        return heure;
    }

    public String getType() {
        return type;
    }

    public String getLieu() {
        return lieu;
    }

    public String getDescription() {
        return description;
    }
}
