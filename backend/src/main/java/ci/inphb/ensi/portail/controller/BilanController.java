package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.facade.BilanFacade;
import ci.inphb.ensi.portail.presentation.dto.BilanDto;
import ci.inphb.ensi.portail.presentation.dto.RubriqueBilanDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ci.inphb.ensi.portail.utils.PdfResponse.pdf;

@RestController
@RequestMapping("/ws/bilan")
@SecurityRequirement(name = "Authorization")
public class BilanController {

    private final BilanFacade bilanFacade;

    public BilanController(BilanFacade bilanFacade) {
        this.bilanFacade = bilanFacade;
    }

    @GetMapping("/resume")
    @Logged
    public BilanDto resume() {
        return bilanFacade.resume();
    }

    @PostMapping("/actif/enregistrer")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public RubriqueBilanDto enregistrerActif(@Valid @RequestBody RubriqueBilanDto dto) {
        return bilanFacade.enregistrerActif(dto);
    }

    @DeleteMapping("/actif/supprimer/{id}")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimerActif(@PathVariable Long id) {
        bilanFacade.supprimerActif(id);
    }

    @PostMapping("/passif/enregistrer")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public RubriqueBilanDto enregistrerPassif(@Valid @RequestBody RubriqueBilanDto dto) {
        return bilanFacade.enregistrerPassif(dto);
    }

    @DeleteMapping("/passif/supprimer/{id}")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimerPassif(@PathVariable Long id) {
        bilanFacade.supprimerPassif(id);
    }

    @GetMapping("/export/pdf")
    @Logged
    public ResponseEntity<byte[]> exporterPdf() {
        return pdf(bilanFacade.exporterPdf(), "bilan.pdf");
    }
}
