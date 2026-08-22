package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.facade.DocumentFacade;
import ci.inphb.ensi.portail.presentation.dto.DocumentDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static ci.inphb.ensi.portail.utils.FichierResponse.attachment;
import static ci.inphb.ensi.portail.utils.FichierResponse.inline;

/**
 * Pieces jointes des proces-verbaux et des evenements. Le bucket restant prive,
 * tout fichier transite par ces endpoints, proteges par le JWT.
 */
@RestController
@RequestMapping("/ws/document")
@SecurityRequirement(name = "Authorization")
public class DocumentController {

    private final DocumentFacade documentFacade;

    public DocumentController(DocumentFacade documentFacade) {
        this.documentFacade = documentFacade;
    }

    @PostMapping(value = "/televerser", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','SECRETAIRE')")
    public DocumentDto televerser(@RequestParam("fichier") MultipartFile fichier,
                                  @RequestParam(value = "pvId", required = false) Long pvId,
                                  @RequestParam(value = "evenementId", required = false) Long evenementId,
                                  @RequestParam(value = "libelle", required = false) String libelle,
                                  @RequestParam(value = "principal", defaultValue = "false") boolean principal) {
        return documentFacade.televerser(fichier, pvId, evenementId, libelle, principal);
    }

    @GetMapping("/lister/pv/{pvId}")
    @Logged
    public List<DocumentDto> listerParPv(@PathVariable Long pvId) {
        return documentFacade.listerParPv(pvId);
    }

    @GetMapping("/lister/evenement/{evenementId}")
    @Logged
    public List<DocumentDto> listerParEvenement(@PathVariable Long evenementId) {
        return documentFacade.listerParEvenement(evenementId);
    }

    @GetMapping("/telecharger/{id}")
    @Logged
    public ResponseEntity<byte[]> telecharger(@PathVariable Long id) {
        DocumentFacade.FichierTelecharge fichier = documentFacade.contenu(id);
        return attachment(fichier.contenu(), fichier.mediaType(), fichier.nomOriginal());
    }

    @GetMapping("/apercu/{id}")
    @Logged
    public ResponseEntity<byte[]> apercu(@PathVariable Long id) {
        DocumentFacade.FichierTelecharge fichier = documentFacade.contenu(id);
        return inline(fichier.contenu(), fichier.mediaType(), fichier.nomOriginal());
    }

    @DeleteMapping("/supprimer/{id}")
    @Logged
    @PreAuthorize("hasAnyRole('ADMIN','SECRETAIRE')")
    public void supprimer(@PathVariable Long id) {
        documentFacade.supprimer(id);
    }
}
