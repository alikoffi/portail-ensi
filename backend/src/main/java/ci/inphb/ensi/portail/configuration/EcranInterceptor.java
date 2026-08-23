package ci.inphb.ensi.portail.configuration;

import ci.inphb.ensi.portail.facade.EcranFacade;
import ci.inphb.ensi.portail.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Refuse les appels d'API rattaches a un ecran masque.
 *
 * Sans ce filtre, masquer un ecran ne ferait que retirer une entree de menu :
 * l'URL resterait accessible et l'API repondrait normalement.
 *
 * L'administrateur en est exempt : c'est lui qui decide du masquage, il doit
 * pouvoir continuer a administrer une section qu'il a retiree aux autres.
 */
@Component
public class EcranInterceptor implements HandlerInterceptor {

    private final EcranFacade ecranFacade;

    public EcranInterceptor(EcranFacade ecranFacade) {
        this.ecranFacade = ecranFacade;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (SecurityUtils.estAdministrateur()) {
            return true;
        }

        String chemin = request.getRequestURI();

        for (String prefixe : ecranFacade.prefixesMasques()) {
            if (chemin.startsWith(prefixe)) {
                repondreMasque(response);
                return false;
            }
        }
        return true;
    }

    private void repondreMasque(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"status\":403,\"error\":\"Forbidden\","
                        + "\"message\":\"Cette section a été désactivée par l'administrateur.\"}");
    }
}
