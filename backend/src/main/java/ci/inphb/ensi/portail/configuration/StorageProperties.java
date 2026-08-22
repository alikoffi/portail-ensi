package ci.inphb.ensi.portail.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Parametrage du stockage des documents (prefixe {@code app.storage}).
 * Le provider vaut {@code local} (disque, developpement) ou {@code b2}
 * (Backblaze B2, compatible S3, production).
 */
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    /** Backend de stockage : "local" (defaut) ou "b2". */
    private String provider = "local";

    /** Dossier racine du stockage local. */
    private String baseDir;

    /** Parametres Backblaze B2 (utilises uniquement si provider=b2). */
    private final B2 b2 = new B2();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public B2 getB2() {
        return b2;
    }

    public static class B2 {

        /** Endpoint S3 du bucket : https://s3.<region>.backblazeb2.com */
        private String endpoint;

        private String bucket;

        private String accessKey;

        private String secretKey;

        /** Region B2, p.ex. "us-west-004" (doit correspondre a l'endpoint). */
        private String region;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }
}
