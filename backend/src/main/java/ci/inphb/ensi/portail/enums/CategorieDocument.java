package ci.inphb.ensi.portail.enums;

import java.util.Locale;
import java.util.Set;

/**
 * Nature d'un document televerse : determine le sous-dossier de stockage,
 * la taille maximale et les types de fichiers acceptes.
 */
public enum CategorieDocument {

    /** Piece jointe d'un proces-verbal (PV scanne et signe, annexes). */
    PV_DOCUMENT("pv", 25, Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "image/webp",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    )),

    /** Piece jointe d'un evenement (affiche, programme, photos). */
    EVENEMENT_DOCUMENT("evenement", 25, Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "image/webp",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    ));

    private final String dossier;
    private final long tailleMaxOctets;
    private final Set<String> typesAutorises;

    CategorieDocument(String dossier, int tailleMaxMo, Set<String> typesAutorises) {
        this.dossier = dossier;
        this.tailleMaxOctets = tailleMaxMo * 1024L * 1024L;
        this.typesAutorises = typesAutorises;
    }

    public String getDossier() {
        return dossier;
    }

    public long getTailleMaxOctets() {
        return tailleMaxOctets;
    }

    public int getTailleMaxMo() {
        return (int) (tailleMaxOctets / 1024L / 1024L);
    }

    public Set<String> getTypesAutorises() {
        return typesAutorises;
    }

    /** Retrouve la categorie a partir du dossier de stockage (lecture d'un fichier). */
    public static CategorieDocument parDossier(String dossier) {
        for (CategorieDocument categorie : values()) {
            if (categorie.dossier.equals(dossier)) {
                return categorie;
            }
        }
        return null;
    }

    /** Tolere la saisie en minuscules ou avec des espaces. */
    public static CategorieDocument depuisLibelle(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        try {
            return valueOf(valeur.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
