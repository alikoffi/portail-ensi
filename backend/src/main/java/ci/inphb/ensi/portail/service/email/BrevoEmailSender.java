package ci.inphb.ensi.portail.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Envoi via l'API HTTPS de Brevo (transactional email) — compatible avec les
 * hebergeurs qui bloquent le SMTP sortant (Render gratuit).
 */
@Component
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "brevo")
public class BrevoEmailSender implements EmailSender {

    private static final String URL_API = "https://api.brevo.com/v3/smtp/email";

    private final RestClient restClient = RestClient.create();

    @Value("${app.mail.brevo.api-key:}")
    private String apiKey;

    @Value("${app.mail.from:no-reply@ensi.ci}")
    private String expediteur;

    @Value("${app.mail.from-name:Portail ENSI}")
    private String nomExpediteur;

    @Override
    public void envoyer(String destinataire, String objet, String htmlContent) {
        Map<String, Object> corps = Map.of(
                "sender", Map.of("email", expediteur, "name", nomExpediteur),
                "to", List.of(Map.of("email", destinataire)),
                "subject", objet,
                "htmlContent", htmlContent
        );
        restClient.post()
                .uri(URL_API)
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(corps)
                .retrieve()
                .toBodilessEntity();
    }
}
