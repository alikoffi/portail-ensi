package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.facade.PvFacade;
import ci.inphb.ensi.portail.presentation.dto.PvDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

import static ci.inphb.ensi.portail.utils.PdfResponse.pdf;

@RestController
@RequestMapping("/ws/pv")
@SecurityRequirement(name = "Authorization")
public class PvController {

    private final PvFacade pvFacade;

    public PvController(PvFacade pvFacade) {
        this.pvFacade = pvFacade;
    }

    @GetMapping("/lister")
    @Logged
    public List<PvDto> lister() {
        return pvFacade.lister();
    }

    @GetMapping("/detail/{id}")
    @Logged
    public PvDto detail(@PathVariable Long id) {
        return pvFacade.detail(id);
    }

    @PostMapping("/enregistrer")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public PvDto enregistrer(@Valid @RequestBody PvDto dto) {
        return pvFacade.enregistrer(dto);
    }

    @PutMapping("/modifier")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public PvDto modifier(@Valid @RequestBody PvDto dto) {
        return pvFacade.modifier(dto);
    }

    @DeleteMapping("/supprimer/{id}")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimer(@PathVariable Long id) {
        pvFacade.supprimer(id);
    }

    @GetMapping("/export/pdf/{id}")
    @Logged
    public ResponseEntity<byte[]> exporterPdf(@PathVariable Long id) {
        return pdf(pvFacade.exporterPdf(id), "proces-verbal-" + id + ".pdf");
    }
}
