package ci.inphb.ensi.portail.domain;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Rubrique du bilan - cote actif.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = BilanActif.TABLE_NAME)
public class BilanActif extends RubriqueBilan {

    public static final String TABLE_NAME = "bilan_actif";
    public static final String TABLE_ID = TABLE_NAME + ID;
    public static final String TABLE_SEQ = TABLE_ID + SEQ;

    @Id
    @SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    private Long id;

    @Override
    public Long getId() {
        return id;
    }
}
