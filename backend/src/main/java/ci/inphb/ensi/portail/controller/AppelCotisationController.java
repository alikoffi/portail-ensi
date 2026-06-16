package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.facade.AppelCotisationFacade;
import ci.inphb.ensi.portail.presentation.dto.AppelCotisationDto;
import ci.inphb.ensi.portail.presentation.dto.AppelDetailDto;
import ci.inphb.ensi.portail.presentation.dto.PaiementDto;
import ci.inphb.ensi.portail.presentation.dto.StatistiqueCotisationDto;
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
@RequestMapping("/ws/appel-cotisation")
@SecurityRequirement(name = "Authorization")
public class AppelCotisationController {

    private final AppelCotisationFacade facade;

    public AppelCotisationController(AppelCotisationFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/lister")
    @Logged
    public List<AppelCotisationDto> lister() {
        return facade.lister();
    }

    @GetMapping("/detail/{id}")
    @Logged
    public AppelDetailDto detail(@PathVariable Long id) {
        return facade.detail(id);
    }

    @GetMapping("/statistiques")
    @Logged
    public StatistiqueCotisationDto statistiques(@RequestParam(required = false) Integer annee) {
        return facade.statistiques(annee);
    }

    @PostMapping("/enregistrer")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','TRESORIER')")
    public AppelCotisationDto enregistrer(@Valid @RequestBody AppelCotisationDto dto) {
        return facade.enregistrer(dto);
    }

    @PutMapping("/modifier")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','TRESORIER')")
    public AppelCotisationDto modifier(@Valid @RequestBody AppelCotisationDto dto) {
        return facade.modifier(dto);
    }

    @DeleteMapping("/supprimer/{id}")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','TRESORIER')")
    public void supprimer(@PathVariable Long id) {
        facade.supprimer(id);
    }

    @PostMapping("/paiement")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','TRESORIER')")
    public void enregistrerPaiement(@Valid @RequestBody PaiementDto dto) {
        facade.enregistrerPaiement(dto);
    }

    @DeleteMapping("/paiement/{appelId}/{membreId}")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','TRESORIER')")
    public void supprimerPaiement(@PathVariable Long appelId, @PathVariable Long membreId) {
        facade.supprimerPaiement(appelId, membreId);
    }
}
