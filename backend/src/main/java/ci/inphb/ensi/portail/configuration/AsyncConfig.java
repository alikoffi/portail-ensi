package ci.inphb.ensi.portail.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Active l'execution asynchrone (envoi d'emails hors du fil de la requete).
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
