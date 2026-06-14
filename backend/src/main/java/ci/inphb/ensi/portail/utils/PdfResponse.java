package ci.inphb.ensi.portail.utils;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Construit une reponse HTTP de telechargement PDF.
 */
public final class PdfResponse {

    private PdfResponse() {
    }

    public static ResponseEntity<byte[]> pdf(byte[] contenu, String nomFichier) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(nomFichier).build());
        headers.setContentLength(contenu.length);
        return new ResponseEntity<>(contenu, headers, 200);
    }
}
