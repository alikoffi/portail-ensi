package ci.inphb.ensi.portail.configuration;

import ci.inphb.ensi.portail.facade.EcranFacade;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Branche le filtrage des ecrans masques sur toutes les routes de l'API.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final EcranInterceptor ecranInterceptor;
    private final EcranFacade ecranFacade;

    public WebMvcConfig(EcranInterceptor ecranInterceptor, EcranFacade ecranFacade) {
        this.ecranInterceptor = ecranInterceptor;
        this.ecranFacade = ecranFacade;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ecranInterceptor).addPathPatterns("/ws/**");
    }

    /** Le cache des prefixes masques doit etre chaud des le premier appel. */
    @EventListener(ApplicationReadyEvent.class)
    public void chargerPrefixesMasques() {
        ecranFacade.rafraichirPrefixesMasques();
    }
}
