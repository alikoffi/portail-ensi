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

import java.util.Arrays;
import java.util.List;

/**
 * Ecran de l'application, que l'administrateur peut retirer de la navigation.
 *
 * Le masquage s'ajoute au controle par role : il n'ouvre jamais un acces, il ne
 * fait qu'en fermer un, pour tout le monde a la fois.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = Ecran.TABLE_NAME)
public class Ecran extends AbstractEntity {

    public static final String TABLE_NAME = "ecran";
    public static final String TABLE_ID = TABLE_NAME + ID;
    public static final String TABLE_SEQ = TABLE_ID + SEQ;

    @Id
    @SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    /** Prefixes d'API couverts, separes par des virgules. */
    @Column(name = "prefixes_api")
    private String prefixesApi;

    /** Un ecran verrouille ne peut pas etre masque. */
    @Column(name = "verrouille", nullable = false)
    private boolean verrouille;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;

    @Column(name = "ordre", nullable = false)
    private int ordre;

    public Ecran() {
    }

    /** Prefixes exploitables, la colonne etant stockee en une seule chaine. */
    public List<String> prefixes() {
        if (prefixesApi == null || prefixesApi.isBlank()) {
            return List.of();
        }
        return Arrays.stream(prefixesApi.split(","))
                .map(String::trim)
                .filter(p -> !p.isEmpty())
                .toList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getPrefixesApi() {
        return prefixesApi;
    }

    public boolean isVerrouille() {
        return verrouille;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getOrdre() {
        return ordre;
    }
}
