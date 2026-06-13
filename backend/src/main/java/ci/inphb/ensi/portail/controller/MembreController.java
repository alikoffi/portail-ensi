package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.facade.MembreFacade;
import ci.inphb.ensi.portail.presentation.dto.MembreDto;
import ci.inphb.ensi.portail.presentation.dto.RecouvrementDto;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ws/membre")
@SecurityRequirement(name = "Authorization")
public class MembreController {

    private final MembreFacade membreFacade;

    public MembreController(MembreFacade membreFacade) {
        this.membreFacade = membreFacade;
    }

    @GetMapping("/lister")
    @Logged
    public List<MembreDto> lister() {
        return membreFacade.lister();
    }

    @GetMapping("/detail/{id}")
    @Logged
    public MembreDto detail(@PathVariable Long id) {
        return membreFacade.detail(id);
    }

    @GetMapping("/recouvrement")
    @Logged
    public RecouvrementDto recouvrement() {
        return membreFacade.recouvrement();
    }

    @PostMapping("/enregistrer")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public MembreDto enregistrer(@Valid @RequestBody MembreDto dto) {
        return membreFacade.enregistrer(dto);
    }

    @PutMapping("/modifier")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public MembreDto modifier(@Valid @RequestBody MembreDto dto) {
        return membreFacade.modifier(dto);
    }

    @DeleteMapping("/supprimer/{id}")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimer(@PathVariable Long id) {
        membreFacade.supprimer(id);
    }
}
