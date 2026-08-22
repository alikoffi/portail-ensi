package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.enums.StatutEvenement;
import ci.inphb.ensi.portail.facade.EvenementFacade;
import ci.inphb.ensi.portail.presentation.dto.EvenementDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ws/evenement")
@SecurityRequirement(name = "Authorization")
public class EvenementController {

    private final EvenementFacade evenementFacade;

    public EvenementController(EvenementFacade evenementFacade) {
        this.evenementFacade = evenementFacade;
    }

    @GetMapping("/lister")
    @Logged
    public List<EvenementDto> lister() {
        return evenementFacade.lister();
    }

    @GetMapping("/detail/{id}")
    @Logged
    public EvenementDto detail(@PathVariable Long id) {
        return evenementFacade.detail(id);
    }

    @PostMapping("/enregistrer")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','SECRETAIRE')")
    public EvenementDto enregistrer(@Valid @RequestBody EvenementDto dto) {
        return evenementFacade.enregistrer(dto);
    }

    @PutMapping("/modifier")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','SECRETAIRE')")
    public EvenementDto modifier(@Valid @RequestBody EvenementDto dto) {
        return evenementFacade.modifier(dto);
    }

    @PutMapping("/statut/{id}")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','SECRETAIRE')")
    public EvenementDto changerStatut(@PathVariable Long id, @RequestParam StatutEvenement statut) {
        return evenementFacade.changerStatut(id, statut);
    }

    @DeleteMapping("/supprimer/{id}")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','SECRETAIRE')")
    public void supprimer(@PathVariable Long id) {
        evenementFacade.supprimer(id);
    }
}
