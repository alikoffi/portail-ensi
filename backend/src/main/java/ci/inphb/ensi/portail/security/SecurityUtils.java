package ci.inphb.ensi.portail.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utilitaires d'acces a l'utilisateur courant.
 */
public final class SecurityUtils {

    public static final String DEFAULT_LOGIN = "ANONYMOUS";

    private SecurityUtils() {
    }

    /**
     * @return le login de l'utilisateur connecte, ou {@link #DEFAULT_LOGIN}.
     */
    public static String lireLoginUtilisateurConnecte() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            Object principal = context.getAuthentication().getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return userDetails.getUsername();
            }
            if (principal instanceof String login) {
                return login;
            }
        }
        return DEFAULT_LOGIN;
    }

    /**
     * @return vrai si l'utilisateur courant est administrateur.
     */
    public static boolean estAdministrateur() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null) {
            return false;
        }
        Authentication authentication = context.getAuthentication();
        for (GrantedAuthority autorite : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(autorite.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
