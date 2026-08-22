package ci.inphb.ensi.portail.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * Client S3 pointant vers Backblaze B2. Instancie uniquement en production
 * (app.storage.provider=b2) ; en local le bean n'existe pas.
 *
 * L'endpoint et la region vont de pair, p.ex. endpoint
 * {@code https://s3.us-west-004.backblazeb2.com} -> region {@code us-west-004}.
 */
@Configuration
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "b2")
public class B2Config {

    @Bean
    public S3Client b2Client(StorageProperties properties) {
        StorageProperties.B2 b2 = properties.getB2();
        return S3Client.builder()
                .endpointOverride(URI.create(b2.getEndpoint()))
                .region(Region.of(b2.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(b2.getAccessKey(), b2.getSecretKey())))
                // B2 exige l'acces "path-style"
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
