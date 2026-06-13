package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Pv;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.PvDto;
import ci.inphb.ensi.portail.repository.PvRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gestion des proces-verbaux.
 */
@Service
public class PvFacade {

    private final PvRepository pvRepository;

    public PvFacade(PvRepository pvRepository) {
        this.pvRepository = pvRepository;
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
