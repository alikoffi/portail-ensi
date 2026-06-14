package ci.inphb.ensi.portail.domain;

import ci.inphb.ensi.portail.enums.StatutMembre;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Membre de la promotion ENSI.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = Membre.TABLE_NAME)
public class Membre extends AbstractEntity {

    public static final String TABLE_NAME = "membre";
    public static final String TABLE_ID = TABLE_NAME + ID;
    public static final String TABLE_SEQ = TABLE_ID + SEQ;

    @Id
    @SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenoms")
    private String prenoms;

    @Column(name = "matricule")
    private String matricule;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "specialite")
    private String specialite;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutMembre statut = StatutMembre.ACTIF;

    @Column(name = "date_adhesion")
    private LocalDate dateAdhesion;

    public Membre() {
    }

    public void mettreAJour(String nom, String prenoms, String matricule, String email,
                            String telephone, String specialite, StatutMembre statut, LocalDate dateAdhesion) {
        this.nom = nom;
        this.prenoms = prenoms;
        this.matricule = matricule;
        this.email = email;
        this.telephone = telephone;
        this.specialite = specialite;
        this.statut = statut;
        this.dateAdhesion = dateAdhesion;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenoms() {
        return prenoms;
    }

    public String getMatricule() {
        return matricule;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getSpecialite() {
        return specialite;
    }

    public StatutMembre getStatut() {
        return statut;
    }

    public LocalDate getDateAdhesion() {
        return dateAdhesion;
    }
}
