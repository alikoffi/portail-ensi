package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.facade.ParametrageFacade;
import ci.inphb.ensi.portail.presentation.dto.ParametrageDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ws/parametrage")
@SecurityRequirement(name = "Authorization")
public class ParametrageController {

    private final ParametrageFacade parametrageFacade;

    public ParametrageController(ParametrageFacade parametrageFacade) {
        this.parametrageFacade = parametrageFacade;
    }

    /** Toutes les valeurs d'une categorie (gestion). */
    @GetMapping("/{categorie}/lister")
    @Logged
    public List<ParametrageDto> lister(@PathVariable String categorie) {
        return parametrageFacade.lister(categorie);
    }

    /** Valeurs actives d'une categorie (listes deroulantes). */
    @GetMapping("/{categorie}/actifs")
    @Logged
    public List<ParametrageDto> listerActifs(@PathVariable String categorie) {
        return parametrageFacade.listerActifs(categorie);
    }

    @PostMapping("/enregistrer")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public ParametrageDto enregistrer(@Valid @RequestBody ParametrageDto dto) {
        return parametrageFacade.enregistrer(dto);
    }

    @DeleteMapping("/supprimer/{id}")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimer(@PathVariable Long id) {
        parametrageFacade.supprimer(id);
    }
}
