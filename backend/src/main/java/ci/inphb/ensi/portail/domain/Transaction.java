package ci.inphb.ensi.portail.domain;

import ci.inphb.ensi.portail.enums.TypeTransaction;
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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Mouvement financier (recette ou depense).
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = Transaction.TABLE_NAME)
public class Transaction extends AbstractEntity {

    public static final String TABLE_NAME = "transaction";
    public static final String TABLE_ID = TABLE_NAME + ID;
    public static final String TABLE_SEQ = TABLE_ID + SEQ;

    @Id
    @SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    private Long id;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeTransaction type;

    @Column(name = "montant", nullable = false)
    private BigDecimal montant;

    @Column(name = "date_tx", nullable = false)
    private LocalDate dateTx;

    @Column(name = "categorie")
    private String categorie;

    @Column(name = "note")
    private String note;

    public Transaction() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public TypeTransaction getType() {
        return type;
    }

    public void setType(TypeTransaction type) {
        this.type = type;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDateTx() {
        return dateTx;
    }

    public void setDateTx(LocalDate dateTx) {
        this.dateTx = dateTx;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
