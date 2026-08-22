package ci.inphb.ensi.portail.utils;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;

/**
 * Construit une reponse HTTP de telechargement (piece jointe) ou de
 * visualisation dans le navigateur (inline).
 */
public final class FichierResponse {

    private FichierResponse() {
    }

    /** Force le telechargement du fichier. */
    public static ResponseEntity<byte[]> attachment(byte[] contenu, MediaType type, String nomFichier) {
        return reponse(contenu, type, ContentDisposition.attachment()
                .filename(nomFichier, StandardCharsets.UTF_8).build());
    }

    /** Affiche le fichier dans le navigateur (apercu PDF ou image). */
    public static ResponseEntity<byte[]> inline(byte[] contenu, MediaType type, String nomFichier) {
        return reponse(contenu, type, ContentDisposition.inline()
                .filename(nomFichier, StandardCharsets.UTF_8).build());
    }

    private static ResponseEntity<byte[]> reponse(byte[] contenu, MediaType type, ContentDisposition disposition) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(type != null ? type : MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(disposition);
        headers.setContentLength(contenu.length);
        return new ResponseEntity<>(contenu, headers, 200);
    }
}
