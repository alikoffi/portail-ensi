package ci.inphb.ensi.portail.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Genere un PDF a partir d'un template Thymeleaf (HTML -> PDF via openhtmltopdf).
 */
@Service
public class PdfService {

    private final HtmlRenderService htmlRenderService;

    public PdfService(HtmlRenderService htmlRenderService) {
        this.htmlRenderService = htmlRenderService;
    }

    public byte[] genererDepuisTemplate(String templateName, Map<String, Object> variables) {
        String html = htmlRenderService.render(templateName, variables);
        return convertir(html);
    }

    private byte[] convertir(String html) {
        try (ByteArrayOutputStream sortie = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(sortie);
            builder.run();
            return sortie.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Echec de la generation du PDF", ex);
        }
    }
}
