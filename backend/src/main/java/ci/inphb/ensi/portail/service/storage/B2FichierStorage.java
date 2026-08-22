package ci.inphb.ensi.portail.service.storage;

import ci.inphb.ensi.portail.configuration.StorageProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Stockage sur Backblaze B2 (API compatible S3), actif en production quand
 * {@code app.storage.provider=b2}. Toute la logique d'entree/sortie vit dans
 * {@link S3FichierStorage}.
 */
@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "b2")
public class B2FichierStorage extends S3FichierStorage {

    public B2FichierStorage(@Qualifier("b2Client") S3Client b2Client, StorageProperties properties) {
        super(b2Client, properties.getB2().getBucket());
    }

    @Override
    public String typeStockage() {
        return "B2";
    }
}
