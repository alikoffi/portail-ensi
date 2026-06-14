package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Evenement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class EvenementDto {

    private Long id;

    @NotBlank(message = "Le nom de l'évènement est obligatoire")
    private String nom;

    @NotNull(message = "La date est obligatoire")
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public LocalDate getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(LocalDate dateEvent) {
        this.dateEvent = dateEvent;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
