package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Cotisation;
import ci.inphb.ensi.portail.domain.Membre;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.CotisationDto;
import ci.inphb.ensi.portail.repository.CotisationRepository;
import ci.inphb.ensi.portail.repository.MembreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gestion des paiements de cotisation.
 */
@Service
public class CotisationFacade {

    private final CotisationRepository cotisationRepository;
    private final MembreRepository membreRepository;

    public CotisationFacade(CotisationRepository cotisationRepository, MembreRepository membreRepository) {
        this.cotisationRepository = cotisationRepository;
        this.membreRepository = membreRepository;
    }

    @Transactional(readOnly = true)
    public List<CotisationDto> listerParMembre(Long membreId) {
        return cotisationRepository.findByMembreIdOrderByDatePaiementDescIdDesc(membreId)
                .stream()
                .map(CotisationDto::new)
                .toList();
    }

    @Transactional
    public CotisationDto enregistrer(CotisationDto dto) {
        Cotisation cotisation = new Cotisation();
        appliquer(cotisation, dto);
        return new CotisationDto(cotisationRepository.save(cotisation));
    }

    @Transactional
    public CotisationDto modifier(CotisationDto dto) {
        if (dto.getId() == null) {
            throw PortailException.nonTrouve("Identifiant de la cotisation manquant");
        }
        Cotisation cotisation = cotisationRepository.findById(dto.getId())
                .orElseThrow(() -> PortailException.nonTrouve("Cotisation introuvable"));
        appliquer(cotisation, dto);
        return new CotisationDto(cotisation);
    }

    @Transactional
    public void supprimer(Long id) {
        Cotisation cotisation = cotisationRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Cotisation introuvable"));
        cotisationRepository.delete(cotisation);
    }

    private void appliquer(Cotisation cotisation, CotisationDto dto) {
        Membre membre = membreRepository.findById(dto.getMembreId())
                .orElseThrow(() -> PortailException.nonTrouve("Membre introuvable"));
        cotisation.mettreAJour(membre, dto.getPeriode().trim(), dto.getMontant(), dto.getDatePaiement(), dto.getNote());
    }
}
