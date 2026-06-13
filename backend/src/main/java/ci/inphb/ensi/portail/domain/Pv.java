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
 * Proces-verbal d'une reunion du bureau executif.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = Pv.TABLE_NAME)
public class Pv extends AbstractEntity {

    public static final String TABLE_NAME = "pv";
    public static final String TABLE_ID = TABLE_NAME + ID;
    public static final String TABLE_SEQ = TABLE_ID + SEQ;

    @Id
    @SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    private Long id;

    @Column(name = "objet", nullable = false)
    private String objet;

    @Column(name = "date_pv", nullable = false)
    private LocalDate datePv;

    @Column(name = "lieu")
    private String lieu;

    @Column(name = "presents")
    private String presents;

    @Column(name = "ordre_du_jour")
    private String ordreDuJour;

    @Column(name = "decisions")
    private String decisions;

    @Column(name = "signataires")
    private String signataires;

    public Pv() {
    }

    /** Met a jour les champs modifiables du proces-verbal. */
    public void mettreAJour(String objet, LocalDate datePv, String lieu, String presents,
                            String ordreDuJour, String decisions, String signataires) {
        this.objet = objet;
        this.datePv = datePv;
        this.lieu = lieu;
        this.presents = presents;
        this.ordreDuJour = ordreDuJour;
        this.decisions = decisions;
        this.signataires = signataires;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getObjet() {
        return objet;
    }

    public LocalDate getDatePv() {
        return datePv;
    }

    public String getLieu() {
        return lieu;
    }

    public String getPresents() {
        return presents;
    }

    public String getOrdreDuJour() {
        return ordreDuJour;
    }

    public String getDecisions() {
        return decisions;
    }

    public String getSignataires() {
        return signataires;
    }
}
