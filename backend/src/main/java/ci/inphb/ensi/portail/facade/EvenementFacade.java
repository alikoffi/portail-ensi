package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Evenement;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.EvenementDto;
import ci.inphb.ensi.portail.repository.EvenementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gestion des evenements du planning.
 */
@Service
public class EvenementFacade {

    private final EvenementRepository evenementRepository;

    public EvenementFacade(EvenementRepository evenementRepository) {
        this.evenementRepository = evenementRepository;
    }

    @Transactional(readOnly = true)
    public List<EvenementDto> lister() {
        return evenementRepository.findAllByOrderByDateEventDesc()
                .stream()
                .map(EvenementDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public EvenementDto detail(Long id) {
        return new EvenementDto(trouver(id));
    }

    @Transactional
    public EvenementDto enregistrer(EvenementDto dto) {
        Evenement evenement = new Evenement();
        evenement.mettreAJour(
                dto.getNom().trim(),
                dto.getDateEvent(),
                dto.getHeure(),
                dto.getType(),
                dto.getLieu(),
                dto.getDescription()
        );
        return new EvenementDto(evenementRepository.save(evenement));
    }

    @Transactional
    public EvenementDto modifier(EvenementDto dto) {
        if (dto.getId() == null) {
            throw PortailException.nonTrouve("Identifiant de l'évènement manquant");
        }
        Evenement evenement = trouver(dto.getId());
        evenement.mettreAJour(
                dto.getNom().trim(),
                dto.getDateEvent(),
                dto.getHeure(),
                dto.getType(),
                dto.getLieu(),
                dto.getDescription()
        );
        return new EvenementDto(evenement);
    }

    @Transactional
    public void supprimer(Long id) {
        Evenement evenement = trouver(id);
        evenementRepository.delete(evenement);
    }

    private Evenement trouver(Long id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Évènement introuvable"));
    }
}
