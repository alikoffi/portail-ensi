package ci.inphb.ensi.portail.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Active l'execution asynchrone (emails) et les taches planifiees (rappels).
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
}
