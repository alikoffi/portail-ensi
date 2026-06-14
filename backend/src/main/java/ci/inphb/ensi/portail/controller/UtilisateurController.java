package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.facade.UtilisateurFacade;
import ci.inphb.ensi.portail.presentation.dto.CreationUtilisateurDto;
import ci.inphb.ensi.portail.presentation.dto.UtilisateurDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/lister")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public List<UtilisateurDto> lister() {
        return utilisateurFacade.lister();
    }

    @PostMapping("/enregistrer")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public UtilisateurDto enregistrer(@Valid @RequestBody CreationUtilisateurDto dto) {
        return utilisateurFacade.enregistrer(dto);
    }

    @PutMapping("/basculer-actif/{id}")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public UtilisateurDto basculerActif(@PathVariable Long id) {
        return utilisateurFacade.basculerActif(id);
    }
}
