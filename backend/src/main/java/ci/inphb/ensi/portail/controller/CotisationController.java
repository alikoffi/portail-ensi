package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.facade.CotisationFacade;
import ci.inphb.ensi.portail.presentation.dto.CotisationDto;
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
@RequestMapping("/ws/cotisation")
@SecurityRequirement(name = "Authorization")
public class CotisationController {

    private final CotisationFacade cotisationFacade;

    public CotisationController(CotisationFacade cotisationFacade) {
        this.cotisationFacade = cotisationFacade;
    }

    @GetMapping("/membre/{membreId}")
    @Logged
    public List<CotisationDto> listerParMembre(@PathVariable Long membreId) {
        return cotisationFacade.listerParMembre(membreId);
    }

    @GetMapping("/statistiques")
    @Logged
    public StatistiqueCotisationDto statistiques(@RequestParam(required = false) Integer annee) {
        return cotisationFacade.statistiques(annee);
    }

    @PostMapping("/enregistrer")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','TRESORIER')")
    public CotisationDto enregistrer(@Valid @RequestBody CotisationDto dto) {
        return cotisationFacade.enregistrer(dto);
    }

    @PutMapping("/modifier")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','TRESORIER')")
    public CotisationDto modifier(@Valid @RequestBody CotisationDto dto) {
        return cotisationFacade.modifier(dto);
    }

    @DeleteMapping("/supprimer/{id}")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','TRESORIER')")
    public void supprimer(@PathVariable Long id) {
        cotisationFacade.supprimer(id);
    }
}
