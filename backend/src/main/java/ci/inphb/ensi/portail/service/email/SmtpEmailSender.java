package ci.inphb.ensi.portail.service.email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Envoi via SMTP (Gmail en local). Defaut si app.mail.provider absent.
 */
@Component
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "smtp", matchIfMissing = true)
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@ensi.ci}")
    private String expediteur;

    public SmtpEmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void envoyer(String destinataire, String objet, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(expediteur);
            helper.setTo(destinataire);
            helper.setSubject(objet);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception ex) {
            throw new IllegalStateException("Echec SMTP : " + ex.getMessage(), ex);
        }
    }
}
