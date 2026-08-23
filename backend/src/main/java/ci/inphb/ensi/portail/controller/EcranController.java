package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.facade.EcranFacade;
import ci.inphb.ensi.portail.presentation.dto.EcranDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ws/ecran")
@SecurityRequirement(name = "Authorization")
public class EcranController {

    private final EcranFacade ecranFacade;

    public EcranController(EcranFacade ecranFacade) {
        this.ecranFacade = ecranFacade;
    }

    /** Tous les ecrans avec leur etat : page d'administration. */
    @GetMapping("/lister")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public List<EcranDto> lister() {
        return ecranFacade.lister();
    }

    /** Ce que le menu doit afficher : ouvert a tout utilisateur connecte. */
    @GetMapping("/visibles")
    @Logged
    public List<EcranDto> visibles() {
        return ecranFacade.listerVisibles();
    }

    @PutMapping("/visibilite/{code}")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public EcranDto changerVisibilite(@PathVariable String code, @RequestParam boolean visible) {
        return ecranFacade.changerVisibilite(code, visible);
    }
}
