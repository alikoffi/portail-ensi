package ci.inphb.ensi.portail.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception metier portant un statut HTTP et un message destine au client.
 */
public class PortailException extends RuntimeException {

    private final HttpStatus status;

    public PortailException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static PortailException identifiantsInvalides() {
        return new PortailException(HttpStatus.UNAUTHORIZED, "Nom d'utilisateur ou mot de passe incorrect");
    }

    public static PortailException nonTrouve(String message) {
        return new PortailException(HttpStatus.NOT_FOUND, message);
    }

    public static PortailException conflit(String message) {
        return new PortailException(HttpStatus.CONFLICT, message);
    }
}
