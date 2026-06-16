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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Appel de cotisation : un libelle, un montant attendu (par membre) et une date butoir.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = AppelCotisation.TABLE_NAME)
public class AppelCotisation extends AbstractEntity {

    public static final String TABLE_NAME = "appel_cotisation";
    public static final String TABLE_ID = TABLE_NAME + ID;
    public static final String TABLE_SEQ = TABLE_ID + SEQ;

    @Id
    @SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    private Long id;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Column(name = "montant_attendu", nullable = false)
    private BigDecimal montantAttendu;

    @Column(name = "date_butoir", nullable = false)
    private LocalDate dateButoir;

    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @Column(name = "cloture", nullable = false)
    private boolean cloture;

    public AppelCotisation() {
    }

    public void mettreAJour(String libelle, BigDecimal montantAttendu, LocalDate dateButoir) {
        this.libelle = libelle;
        this.montantAttendu = montantAttendu;
        this.dateButoir = dateButoir;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public BigDecimal getMontantAttendu() {
        return montantAttendu;
    }

    public LocalDate getDateButoir() {
        return dateButoir;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isCloture() {
        return cloture;
    }

    public void setCloture(boolean cloture) {
        this.cloture = cloture;
    }
}
