package ci.inphb.ensi.portail.service.storage;

import ci.inphb.ensi.portail.configuration.StorageProperties;
import ci.inphb.ensi.portail.exception.PortailException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Stockage sur le disque local - utilise en developpement (defaut).
 * Le dossier racine est defini par {@code app.storage.base-dir}.
 */
@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalFichierStorage extends AbstractFichierStorage {

    private final Path racine;

    public LocalFichierStorage(StorageProperties properties) {
        if (!StringUtils.hasText(properties.getBaseDir())) {
            throw new IllegalStateException("Le dossier de stockage (app.storage.base-dir) n'est pas configure.");
        }
        this.racine = Paths.get(properties.getBaseDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.racine);
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible de creer le dossier de stockage local.", exception);
        }
    }

    @Override
    public String typeStockage() {
        return "LOCAL";
    }

    @Override
    protected void ecrire(String dossier, String nomStocke, MultipartFile fichier) {
        Path dossierCible = racine.resolve(dossier).normalize();
        try {
            Files.createDirectories(dossierCible);
            Path cible = resoudre(dossierCible, nomStocke);
            try (InputStream flux = fichier.getInputStream()) {
                Files.copy(flux, cible, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible d'enregistrer le fichier sur le disque local.", exception);
        }
    }

    @Override
    protected ContenuFichier relire(String dossier, String nomStocke) {
        Path chemin = resoudre(racine.resolve(dossier).normalize(), nomStocke);
        if (!Files.exists(chemin) || !Files.isReadable(chemin)) {
            throw PortailException.nonTrouve("Fichier introuvable.");
        }
        try {
            MediaType type = MediaTypeFactory.getMediaType(nomStocke).orElse(MediaType.APPLICATION_OCTET_STREAM);
            return new ContenuFichier(Files.readAllBytes(chemin), type);
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible de lire le fichier sur le disque local.", exception);
        }
    }

    @Override
    protected void effacer(String dossier, String nomStocke) {
        Path chemin = resoudre(racine.resolve(dossier).normalize(), nomStocke);
        try {
            Files.deleteIfExists(chemin);
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible de supprimer le fichier du disque local.", exception);
        }
    }

    /** Empeche toute sortie du dossier cible (traversee de chemin). */
    private Path resoudre(Path dossierCible, String nomStocke) {
        Path chemin = dossierCible.resolve(nomStocke).normalize();
        if (!chemin.startsWith(dossierCible)) {
            throw PortailException.nonTrouve("Fichier introuvable.");
        }
        return chemin;
    }
}
