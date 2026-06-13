package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.BilanActif;
import ci.inphb.ensi.portail.domain.BilanPassif;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.BilanDto;
import ci.inphb.ensi.portail.presentation.dto.RubriqueBilanDto;
import ci.inphb.ensi.portail.repository.BilanActifRepository;
import ci.inphb.ensi.portail.repository.BilanPassifRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Gestion du bilan simplifie (rubriques actif/passif).
 */
@Service
public class BilanFacade {

    private final BilanActifRepository actifRepository;
    private final BilanPassifRepository passifRepository;

    public BilanFacade(BilanActifRepository actifRepository, BilanPassifRepository passifRepository) {
        this.actifRepository = actifRepository;
        this.passifRepository = passifRepository;
    }

    @Transactional(readOnly = true)
    public BilanDto resume() {
        BigDecimal totalActif = actifRepository.total();
        BigDecimal totalPassif = passifRepository.total();

        BilanDto dto = new BilanDto();
        dto.setActifs(actifRepository.findAllByOrderByIdAsc().stream().map(RubriqueBilanDto::new).toList());
        dto.setPassifs(passifRepository.findAllByOrderByIdAsc().stream().map(RubriqueBilanDto::new).toList());
        dto.setTotalActif(totalActif);
        dto.setTotalPassif(totalPassif);
        dto.setEquilibre(totalActif.compareTo(totalPassif) == 0);
        return dto;
    }

    // ---------- ACTIF ----------
    @Transactional
    public RubriqueBilanDto enregistrerActif(RubriqueBilanDto dto) {
        BilanActif rubrique = dto.getId() == null
                ? new BilanActif()
                : actifRepository.findById(dto.getId())
                        .orElseThrow(() -> PortailException.nonTrouve("Rubrique actif introuvable"));
        rubrique.mettreAJour(dto.getRubrique().trim(), dto.getMontant());
        return new RubriqueBilanDto(actifRepository.save(rubrique));
    }

    @Transactional
    public void supprimerActif(Long id) {
        BilanActif rubrique = actifRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Rubrique actif introuvable"));
        actifRepository.delete(rubrique);
    }

    // ---------- PASSIF ----------
    @Transactional
    public RubriqueBilanDto enregistrerPassif(RubriqueBilanDto dto) {
        BilanPassif rubrique = dto.getId() == null
                ? new BilanPassif()
                : passifRepository.findById(dto.getId())
                        .orElseThrow(() -> PortailException.nonTrouve("Rubrique passif introuvable"));
        rubrique.mettreAJour(dto.getRubrique().trim(), dto.getMontant());
        return new RubriqueBilanDto(passifRepository.save(rubrique));
    }

    @Transactional
    public void supprimerPassif(Long id) {
        BilanPassif rubrique = passifRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Rubrique passif introuvable"));
        passifRepository.delete(rubrique);
    }
}
