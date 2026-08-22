package ci.inphb.ensi.portail.service.storage;

import ci.inphb.ensi.portail.enums.CategorieDocument;
import ci.inphb.ensi.portail.exception.PortailException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

/**
 * Logique commune a tous les backends de stockage : validation (taille et type
 * reel via les octets d'en-tete), generation du nom de fichier et garde-fous
 * sur les chemins.
 *
 * Les implementations concretes (disque, B2) ne fournissent que les primitives
 * d'entree/sortie : {@link #ecrire}, {@link #relire} et {@link #effacer}.
 */
public abstract class AbstractFichierStorage implements FichierStorage {

    private static final String MIME_INCONNU = "application/octet-stream";
    private static final String MIME_ZIP = "application/zip";
    private static final String MIME_DOCX =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    // ---------- API commune ----------

    @Override
    public FichierStocke enregistrer(MultipartFile fichier, CategorieDocument categorie) {
        if (categorie == null) {
            throw PortailException.requeteInvalide("La catégorie de document est requise.");
        }
        if (fichier == null || fichier.isEmpty()) {
            throw PortailException.requeteInvalide("Aucun fichier n'a été fourni.");
        }
        if (fichier.getSize() > categorie.getTailleMaxOctets()) {
            throw PortailException.requeteInvalide(
                    "Fichier trop volumineux (max " + categorie.getTailleMaxMo() + " Mo).");
        }

        String typeReel = detecterType(fichier);
        if (!categorie.getTypesAutorises().contains(typeReel)) {
            throw PortailException.requeteInvalide(
                    "Type de fichier non autorisé. Formats acceptés : PDF, image (JPG, PNG, WEBP) ou Word.");
        }

        String dossier = categorie.getDossier();
        String nomStocke = construireNom(fichier.getOriginalFilename());
        ecrire(dossier, nomStocke, fichier);

        return new FichierStocke(
                dossier,
                nomStocke,
                StringUtils.hasText(fichier.getOriginalFilename()) ? fichier.getOriginalFilename() : nomStocke,
                typeReel,
                fichier.getSize()
        );
    }

    @Override
    public ContenuFichier lire(String dossier, String nomStocke) {
        return relire(verifierDossier(dossier), verifierNom(nomStocke));
    }

    @Override
    public void supprimer(String dossier, String nomStocke) {
        effacer(verifierDossier(dossier), verifierNom(nomStocke));
    }

    // ---------- Primitives a implementer ----------

    /** Ecrit le contenu du fichier sous la cle {@code dossier/nomStocke}. */
    protected abstract void ecrire(String dossier, String nomStocke, MultipartFile fichier);

    /** Relit la ressource {@code dossier/nomStocke} (404 si absente). */
    protected abstract ContenuFichier relire(String dossier, String nomStocke);

    /** Supprime la ressource {@code dossier/nomStocke}, sans echouer si absente. */
    protected abstract void effacer(String dossier, String nomStocke);

    // ---------- Helpers partages ----------

    /**
     * Determine le type reel a partir des premiers octets : un executable
     * renomme en .pdf est ainsi rejete. Les formats Office etant des archives
     * ZIP, l'extension sert de depart pour les distinguer.
     */
    private String detecterType(MultipartFile fichier) {
        byte[] entete;
        try {
            byte[] contenu = fichier.getBytes();
            entete = new byte[Math.min(12, contenu.length)];
            System.arraycopy(contenu, 0, entete, 0, entete.length);
        } catch (IOException exception) {
            throw PortailException.requeteInvalide("Impossible de lire le fichier pour validation.");
        }

        if (entete.length < 4) {
            return MIME_INCONNU;
        }
        if ((entete[0] & 0xFF) == 0x25 && entete[1] == 0x50 && entete[2] == 0x44 && entete[3] == 0x46) {
            return "application/pdf";
        }
        if ((entete[0] & 0xFF) == 0xFF && (entete[1] & 0xFF) == 0xD8 && (entete[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if ((entete[0] & 0xFF) == 0x89 && entete[1] == 0x50 && entete[2] == 0x4E && entete[3] == 0x47) {
            return "image/png";
        }
        if (entete.length >= 12 && entete[0] == 'R' && entete[1] == 'I' && entete[2] == 'F' && entete[3] == 'F'
                && entete[8] == 'W' && entete[9] == 'E' && entete[10] == 'B' && entete[11] == 'P') {
            return "image/webp";
        }
        if (entete[0] == 0x50 && entete[1] == 0x4B) {
            String extension = StringUtils.getFilenameExtension(fichier.getOriginalFilename());
            return "docx".equalsIgnoreCase(extension) ? MIME_DOCX : MIME_ZIP;
        }
        return MIME_INCONNU;
    }

    /** {@code rapport final.pdf} devient {@code rapport-final-<horodatage>-<alea>.pdf}. */
    private String construireNom(String nomOriginal) {
        String nom = StringUtils.hasText(nomOriginal) ? nomOriginal : "document";
        String extension = StringUtils.getFilenameExtension(nom);
        String base = StringUtils.stripFilenameExtension(nom)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (!StringUtils.hasText(base)) {
            base = "document";
        }
        if (base.length() > 60) {
            base = base.substring(0, 60);
        }

        String suffixe = Instant.now().toEpochMilli() + "-" + UUID.randomUUID().toString().substring(0, 8);
        return StringUtils.hasText(extension)
                ? base + "-" + suffixe + "." + extension.toLowerCase(Locale.ROOT)
                : base + "-" + suffixe;
    }

    private String verifierDossier(String dossier) {
        if (CategorieDocument.parDossier(dossier) == null) {
            throw PortailException.nonTrouve("Fichier introuvable.");
        }
        return dossier;
    }

    private String verifierNom(String nomStocke) {
        if (!StringUtils.hasText(nomStocke)
                || nomStocke.contains("/") || nomStocke.contains("\\") || nomStocke.contains("..")) {
            throw PortailException.nonTrouve("Fichier introuvable.");
        }
        return nomStocke;
    }
}
