package ci.inphb.ensi.portail.controller;

import ci.inphb.ensi.portail.configuration.logger.Logged;
import ci.inphb.ensi.portail.facade.TableauBordFacade;
import ci.inphb.ensi.portail.presentation.dto.TableauBordDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ws/tableau-de-bord")
@SecurityRequirement(name = "Authorization")
public class TableauBordController {

    private final TableauBordFacade tableauBordFacade;

    public TableauBordController(TableauBordFacade tableauBordFacade) {
        this.tableauBordFacade = tableauBordFacade;
    }

    @GetMapping("/resume")
    @Logged
    public TableauBordDto resume() {
        return tableauBordFacade.resume();
    }
}
