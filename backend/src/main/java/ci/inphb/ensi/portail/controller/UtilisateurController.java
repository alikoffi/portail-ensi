package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.facade.UtilisateurFacade;
import ci.inphb.ensi.portail.presentation.dto.UtilisateurDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ws/utilisateur")
@SecurityRequirement(name = "Authorization")
public class UtilisateurController {

    private final UtilisateurFacade utilisateurFacade;

    public UtilisateurController(UtilisateurFacade utilisateurFacade) {
        this.utilisateurFacade = utilisateurFacade;
    }

    /** Profil de l'utilisateur connecte (valide aussi le token cote front). */
    @GetMapping("/moi")
    @Logged
    public UtilisateurDto moi(Authentication authentication) {
        return utilisateurFacade.parUsername(authentication.getName());
    }

    /** Liste des comptes (reserve a l'administrateur). */
    @GetMapping("/lister")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public List<UtilisateurDto> lister() {
        return utilisateurFacade.lister();
    }
}
