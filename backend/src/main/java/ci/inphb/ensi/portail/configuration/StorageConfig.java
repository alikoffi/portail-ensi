package ci.inphb.ensi.portail.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Active le parametrage du stockage des documents.
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {
}
