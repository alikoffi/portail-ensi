package ci.inphb.ensi.portail.service.storage;

import ci.inphb.ensi.portail.enums.CategorieDocument;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Stockage des fichiers televerses. L'implementation concrete est choisie via
 * la propriete {@code app.storage.provider} :
 *   - local (defaut) : disque, pour le developpement ;
 *   - b2             : Backblaze B2 (compatible S3), pour la production.
 *
 * Le fichier est toujours servi par le backend ({@code /ws/document/...}) :
 * le bucket reste prive, aucune URL de stockage n'est exposee au frontend.
 */
public interface FichierStorage {

    /** Valide puis enregistre le fichier ; retourne les references a persister. */
    FichierStocke enregistrer(MultipartFile fichier, CategorieDocument categorie);

    /** Relit un fichier precedemment enregistre. */
    ContenuFichier lire(String dossier, String nomStocke);

    /** Supprime un fichier (sans echouer s'il n'existe plus). */
    void supprimer(String dossier, String nomStocke);

    /** Identifiant du backend, trace en base : LOCAL ou B2. */
    String typeStockage();

    /** References d'un fichier enregistre. */
    record FichierStocke(String dossier, String nomStocke, String nomOriginal, String contentType, long taille) {
    }

    /** Contenu binaire d'un fichier relu. */
    record ContenuFichier(byte[] contenu, MediaType mediaType) {
    }
}
