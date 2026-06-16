package ci.inphb.ensi.portail.service;

import ci.inphb.ensi.portail.domain.Utilisateur;
import ci.inphb.ensi.portail.presentation.dto.EvenementDto;
import ci.inphb.ensi.portail.service.email.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Envoi d'emails (HTML via templates Thymeleaf), de maniere asynchrone.
 * Le transport (SMTP / Brevo) est fourni par {@link EmailSender}.
 */
@Service
public class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    private final EmailSender emailSender;
    private final HtmlRenderService htmlRenderService;

    @Value("${app.mail.enabled:false}")
    private boolean mailActive;

    @Value("${app.base-url:http://localhost:4200}")
    private String urlApplication;

    public MailService(EmailSender emailSender, HtmlRenderService htmlRenderService) {
        this.emailSender = emailSender;
        this.htmlRenderService = htmlRenderService;
    }

    /**
     * Email de bienvenue a la creation d'un compte : lien de l'application + identifiants.
     */
    @Async
    public void envoyerBienvenue(Utilisateur utilisateur, String motDePasseClair) {
        if (!StringUtils.hasText(utilisateur.getEmail())) {
            return;
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("label", StringUtils.hasText(utilisateur.getLabel()) ? utilisateur.getLabel() : utilisateur.getUsername());
        variables.put("username", utilisateur.getUsername());
        variables.put("motDePasse", motDePasseClair);
        variables.put("role", utilisateur.getRole().name());
        variables.put("lien", urlApplication);
        envoyer(utilisateur.getEmail(), "Votre accès au Portail ENSI", "mail/bienvenue", variables);
    }

    /**
     * Email de rappel : evenements a venir et membres en retard de cotisation.
     */
    @Async
    public void envoyerRappel(Utilisateur destinataire, List<EvenementDto> evenements, List<String> membresEnRetard) {
        if (!StringUtils.hasText(destinataire.getEmail())) {
            return;
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("label", StringUtils.hasText(destinataire.getLabel()) ? destinataire.getLabel() : destinataire.getUsername());
        variables.put("evenements", evenements);
        variables.put("membresEnRetard", membresEnRetard);
        variables.put("lien", urlApplication);
        envoyer(destinataire.getEmail(), "Rappel — Portail ENSI", "mail/rappel", variables);
    }

    private void envoyer(String destinataire, String objet, String template, Map<String, Object> variables) {
        if (!mailActive) {
            LOGGER.info("Envoi d'email desactive (app.mail.enabled=false) - destinataire {}", destinataire);
            return;
        }
        try {
            String contenu = htmlRenderService.render(template, variables);
            emailSender.envoyer(destinataire, objet, contenu);
            LOGGER.info("Email '{}' envoye a {}", objet, destinataire);
        } catch (Exception ex) {
            LOGGER.error("Echec d'envoi d'email a {} : {}", destinataire, ex.getMessage());
        }
    }
}
