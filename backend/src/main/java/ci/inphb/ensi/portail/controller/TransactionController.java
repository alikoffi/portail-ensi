package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.facade.TransactionFacade;
import ci.inphb.ensi.portail.presentation.dto.TransactionDto;
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
@RequestMapping("/ws/transaction")
@SecurityRequirement(name = "Authorization")
public class TransactionController {

    private final TransactionFacade transactionFacade;

    public TransactionController(TransactionFacade transactionFacade) {
        this.transactionFacade = transactionFacade;
    }

    @GetMapping("/lister")
    @Logged
    public List<TransactionDto> lister() {
        return transactionFacade.lister();
    }

    @PostMapping("/enregistrer")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public TransactionDto enregistrer(@Valid @RequestBody TransactionDto dto) {
        return transactionFacade.enregistrer(dto);
    }

    @PutMapping("/modifier")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public TransactionDto modifier(@Valid @RequestBody TransactionDto dto) {
        return transactionFacade.modifier(dto);
    }

    @DeleteMapping("/supprimer/{id}")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public void supprimer(@PathVariable Long id) {
        transactionFacade.supprimer(id);
    }

    @GetMapping("/export/pdf")
    @Logged
    public ResponseEntity<byte[]> exporterPdf() {
        return pdf(transactionFacade.exporterPdf(), "journal-transactions.pdf");
    }
}
