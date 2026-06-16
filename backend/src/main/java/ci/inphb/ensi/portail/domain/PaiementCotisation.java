package ci.inphb.ensi.portail.domain;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Montant verse par un membre pour un appel de cotisation donne.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = PaiementCotisation.TABLE_NAME)
public class PaiementCotisation extends AbstractEntity {

    public static final String TABLE_NAME = "paiement_cotisation";
    public static final String TABLE_ID = TABLE_NAME + ID;
    public static final String TABLE_SEQ = TABLE_ID + SEQ;

    @Id
    @SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appel_id", nullable = false)
    private AppelCotisation appel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    @Column(name = "montant", nullable = false)
    private BigDecimal montant;

    @Column(name = "date_paiement", nullable = false)
    private LocalDate datePaiement;

    @Column(name = "note")
    private String note;

    public PaiementCotisation() {
    }

    public PaiementCotisation(AppelCotisation appel, Membre membre) {
        this.appel = appel;
        this.membre = membre;
    }

    public void mettreAJour(BigDecimal montant, LocalDate datePaiement, String note) {
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.note = note;
    }

    @Override
    public Long getId() {
        return id;
    }

    public AppelCotisation getAppel() {
        return appel;
    }

    public Membre getMembre() {
        return membre;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public String getNote() {
        return note;
    }
}
