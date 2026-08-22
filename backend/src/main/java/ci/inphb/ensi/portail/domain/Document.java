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

/**
 * Fichier televerse et rattache a un proces-verbal ou a un evenement.
 * Le contenu binaire vit dans le stockage (disque en local, Backblaze B2 en
 * production) ; seules ses references sont conservees ici.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = Document.TABLE_NAME)
public class Document extends AbstractEntity {

    public static final String TABLE_NAME = "document";
    public static final String TABLE_ID = TABLE_NAME + ID;
    public static final String TABLE_SEQ = TABLE_ID + SEQ;

    @Id
    @SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pv_id")
    private Pv pv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @Column(name = "libelle")
    private String libelle;

    @Column(name = "nom_original", nullable = false)
    private String nomOriginal;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "taille", nullable = false)
    private long taille;

    @Column(name = "storage_type", nullable = false)
    private String storageType;

    @Column(name = "dossier", nullable = false)
    private String dossier;

    @Column(name = "nom_stocke", nullable = false)
    private String nomStocke;

    @Column(name = "principal", nullable = false)
    private boolean principal;

    public Document() {
    }

    /** Renseigne les references du fichier au moment du televersement. */
    public void referencer(String dossier, String nomStocke, String nomOriginal,
                           String contentType, long taille, String storageType) {
        this.dossier = dossier;
        this.nomStocke = nomStocke;
        this.nomOriginal = nomOriginal;
        this.contentType = contentType;
        this.taille = taille;
        this.storageType = storageType;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Pv getPv() {
        return pv;
    }

    public void setPv(Pv pv) {
        this.pv = pv;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getNomOriginal() {
        return nomOriginal;
    }

    public String getContentType() {
        return contentType;
    }

    public long getTaille() {
        return taille;
    }

    public String getStorageType() {
        return storageType;
    }

    public String getDossier() {
        return dossier;
    }

    public String getNomStocke() {
        return nomStocke;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
}
