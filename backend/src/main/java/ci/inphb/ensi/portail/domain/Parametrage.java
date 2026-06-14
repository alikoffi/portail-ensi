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

/**
 * Valeur parametrable (liste de reference).
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = Parametrage.TABLE_NAME)
public class Parametrage extends AbstractEntity {

    public static final String TABLE_NAME = "parametrage";
    public static final String TABLE_ID = TABLE_NAME + ID;
    public static final String TABLE_SEQ = TABLE_ID + SEQ;

    @Id
    @SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    private Long id;

    @Column(name = "categorie", nullable = false)
    private String categorie;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Column(name = "ordre", nullable = false)
    private int ordre;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    public Parametrage() {
    }

    public void mettreAJour(String categorie, String libelle, int ordre, boolean actif) {
        this.categorie = categorie;
        this.libelle = libelle;
        this.ordre = ordre;
        this.actif = actif;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getLibelle() {
        return libelle;
    }

    public int getOrdre() {
        return ordre;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
