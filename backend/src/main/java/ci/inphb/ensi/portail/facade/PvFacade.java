package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Pv;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.PvDto;
import ci.inphb.ensi.portail.repository.PvRepository;
import ci.inphb.ensi.portail.service.PdfService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestion des proces-verbaux.
 */
@Service
public class PvFacade {

    private final PvRepository pvRepository;
    private final PdfService pdfService;

    public PvFacade(PvRepository pvRepository, PdfService pdfService) {
        this.pvRepository = pvRepository;
        this.pdfService = pdfService;
    }

    @Transactional(readOnly = true)
    public List<PvDto> lister() {
        return pvRepository.findAllByOrderByDatePvDesc()
                .stream()
                .map(PvDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public PvDto detail(Long id) {
        return new PvDto(trouver(id));
    }

    @Transactional
    public PvDto enregistrer(PvDto dto) {
        Pv pv = new Pv();
        appliquer(pv, dto);
        return new PvDto(pvRepository.save(pv));
    }

    @Transactional
    public PvDto modifier(PvDto dto) {
        if (dto.getId() == null) {
            throw PortailException.nonTrouve("Identifiant du procès-verbal manquant");
        }
        Pv pv = trouver(dto.getId());
        appliquer(pv, dto);
        return new PvDto(pv);
    }

    @Transactional
    public void supprimer(Long id) {
        pvRepository.delete(trouver(id));
    }

    @Transactional(readOnly = true)
    public byte[] exporterPdf(Long id) {
        PvDto pv = new PvDto(trouver(id));
        Map<String, Object> variables = new HashMap<>();
        variables.put("pv", pv);
        variables.put("dateEdition", LocalDate.now());
        return pdfService.genererDepuisTemplate("pdf/pv", variables);
    }

    private void appliquer(Pv pv, PvDto dto) {
        pv.mettreAJour(
                dto.getObjet().trim(),
                dto.getDatePv(),
                dto.getLieu(),
                dto.getPresents(),
                dto.getOrdreDuJour(),
                dto.getDecisions(),
                dto.getSignataires()
        );
    }

    private Pv trouver(Long id) {
        return pvRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Procès-verbal introuvable"));
    }
}
