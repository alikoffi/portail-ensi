package ci.inphb.ensi.portail.domain;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Evenement du planning de la promotion.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = Evenement.TABLE_NAME)
public class Evenement extends AbstractEntity {

    public static final String TABLE_NAME = "evenement";
    public static final String TABLE_ID = TABLE_NAME + ID;
    public static final String TABLE_SEQ = TABLE_ID + SEQ;

    @Id
    @SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "date_event", nullable = false)
    private LocalDate dateEvent;

    @Column(name = "heure")
    private String heure;

    @Column(name = "type")
    private String type;

    @Column(name = "lieu")
    private String lieu;

    @Column(name = "description")
    private String description;

    public Evenement() {
    }

    /** Met a jour les champs modifiables de l'evenement. */
    public void mettreAJour(String nom, LocalDate dateEvent, String heure, String type, String lieu, String description) {
        this.nom = nom;
        this.dateEvent = dateEvent;
        this.heure = heure;
        this.type = type;
        this.lieu = lieu;
        this.description = description;
    }

    @Override
    public Long getId() {
        return id;
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
