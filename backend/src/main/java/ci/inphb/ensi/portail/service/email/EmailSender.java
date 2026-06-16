package ci.inphb.ensi.portail.service.email;

/**
 * Envoi bas niveau d'un email HTML. Implementation choisie via app.mail.provider
 * (smtp en local, brevo en production - API HTTPS compatible Render gratuit).
 */
public interface EmailSender {

    void envoyer(String destinataire, String objet, String htmlContent);
}
