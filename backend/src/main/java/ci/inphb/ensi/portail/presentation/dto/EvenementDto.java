package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Evenement;
import ci.inphb.ensi.portail.enums.EtatEvenement;
import ci.inphb.ensi.portail.enums.StatutEvenement;
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

    /** Statut saisi par le bureau (PLANIFIE par defaut). */
    private StatutEvenement statut;

    /** Etat affiche, derive du statut et de la date : lecture seule. */
    private EtatEvenement etat;

    /** Proces-verbal rattache, s'il existe (renseigne par la facade). */
    private Long pvId;
    private String pvObjet;
    private long nbDocuments;

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
        this.statut = e.getStatut();
        this.etat = EtatEvenement.calculer(e.getStatut(), e.getDateEvent());
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

    public StatutEvenement getStatut() {
        return statut;
    }

    public void setStatut(StatutEvenement statut) {
        this.statut = statut;
    }

    public EtatEvenement getEtat() {
        return etat;
    }

    public void setEtat(EtatEvenement etat) {
        this.etat = etat;
    }

    public Long getPvId() {
        return pvId;
    }

    public void setPvId(Long pvId) {
        this.pvId = pvId;
    }

    public String getPvObjet() {
        return pvObjet;
    }

    public void setPvObjet(String pvObjet) {
        this.pvObjet = pvObjet;
    }

    public long getNbDocuments() {
        return nbDocuments;
    }

    public void setNbDocuments(long nbDocuments) {
        this.nbDocuments = nbDocuments;
    }
}
