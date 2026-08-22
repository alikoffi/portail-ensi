package ci.inphb.ensi.portail.service.storage;

import ci.inphb.ensi.portail.exception.PortailException;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

/**
 * Stockage sur un service objet compatible S3. Les fichiers sont ranges sous
 * la cle {@code dossier/nom} dans un bucket prive ; ils restent servis par le
 * backend, si bien que la base et le frontend ignorent le backend reel.
 */
public abstract class S3FichierStorage extends AbstractFichierStorage {

    private final S3Client client;
    private final String bucket;

    protected S3FichierStorage(S3Client client, String bucket) {
        this.client = client;
        this.bucket = bucket;
    }

    @Override
    protected void ecrire(String dossier, String nomStocke, MultipartFile fichier) {
        try {
            PutObjectRequest requete = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(cle(dossier, nomStocke))
                    .contentType(fichier.getContentType())
                    .contentLength(fichier.getSize())
                    .build();
            client.putObject(requete, RequestBody.fromInputStream(fichier.getInputStream(), fichier.getSize()));
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible d'envoyer le fichier vers le stockage objet.", exception);
        }
    }

    @Override
    protected ContenuFichier relire(String dossier, String nomStocke) {
        try {
            ResponseBytes<GetObjectResponse> objet = client.getObjectAsBytes(
                    GetObjectRequest.builder().bucket(bucket).key(cle(dossier, nomStocke)).build());
            return new ContenuFichier(objet.asByteArray(), typeMedia(objet.response().contentType(), nomStocke));
        } catch (NoSuchKeyException exception) {
            throw PortailException.nonTrouve("Fichier introuvable.");
        }
    }

    @Override
    protected void effacer(String dossier, String nomStocke) {
        client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(cle(dossier, nomStocke)).build());
    }

    private String cle(String dossier, String nomStocke) {
        return dossier + "/" + nomStocke;
    }

    private MediaType typeMedia(String contentType, String nomStocke) {
        if (contentType != null && !contentType.isBlank()) {
            try {
                return MediaType.parseMediaType(contentType);
            } catch (RuntimeException ignored) {
                // on retombe sur la detection par nom de fichier
            }
        }
        return MediaTypeFactory.getMediaType(nomStocke).orElse(MediaType.APPLICATION_OCTET_STREAM);
    }
}
