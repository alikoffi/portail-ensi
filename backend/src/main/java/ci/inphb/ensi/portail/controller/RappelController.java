package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.presentation.dto.RappelResultatDto;
import ci.inphb.ensi.portail.service.RappelService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ws/rappel")
@SecurityRequirement(name = "Authorization")
public class RappelController {

    private final RappelService rappelService;

    public RappelController(RappelService rappelService) {
        this.rappelService = rappelService;
    }

    /** Declenchement manuel des rappels (administrateur). */
    @PostMapping("/envoyer")
    @Logged
    @PreAuthorize("hasRole('ADMIN')")
    public RappelResultatDto envoyer() {
        return rappelService.envoyerRappels();
    }
}
