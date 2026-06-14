package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Membre;
import ci.inphb.ensi.portail.enums.StatutMembre;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.MembreDto;
import ci.inphb.ensi.portail.presentation.dto.MembreRecouvrementDto;
import ci.inphb.ensi.portail.presentation.dto.RecouvrementDto;
import ci.inphb.ensi.portail.repository.CotisationRepository;
import ci.inphb.ensi.portail.repository.MembreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestion des membres de la promotion et du recouvrement des cotisations.
 */
@Service
public class MembreFacade {

    private final MembreRepository membreRepository;
    private final CotisationRepository cotisationRepository;

    public MembreFacade(MembreRepository membreRepository, CotisationRepository cotisationRepository) {
        this.membreRepository = membreRepository;
        this.cotisationRepository = cotisationRepository;
    }

    @Transactional(readOnly = true)
    public List<MembreDto> lister() {
        return membreRepository.findAllByOrderByNomAscPrenomsAsc()
                .stream()
                .map(MembreDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public MembreDto detail(Long id) {
        return new MembreDto(trouver(id));
    }

    @Transactional
    public MembreDto enregistrer(MembreDto dto) {
        Membre membre = new Membre();
        appliquer(membre, dto);
        return new MembreDto(membreRepository.save(membre));
    }

    @Transactional
    public MembreDto modifier(MembreDto dto) {
        if (dto.getId() == null) {
            throw PortailException.nonTrouve("Identifiant du membre manquant");
        }
        Membre membre = trouver(dto.getId());
        appliquer(membre, dto);
        return new MembreDto(membre);
    }

    @Transactional
    public void supprimer(Long id) {
        membreRepository.delete(trouver(id));
    }

    @Transactional(readOnly = true)
    public RecouvrementDto recouvrement() {
        Map<Long, BigDecimal> totalParMembre = new HashMap<>();
        Map<Long, Long> nbParMembre = new HashMap<>();
        Map<Long, LocalDate> dernierParMembre = new HashMap<>();
        for (Object[] ligne : cotisationRepository.agregatParMembre()) {
            Long membreId = (Long) ligne[0];
            totalParMembre.put(membreId, (BigDecimal) ligne[1]);
            nbParMembre.put(membreId, (Long) ligne[2]);
            dernierParMembre.put(membreId, (LocalDate) ligne[3]);
        }

        List<Membre> membres = membreRepository.findAllByOrderByNomAscPrenomsAsc();
        List<MembreRecouvrementDto> lignes = new ArrayList<>();
        long nbActifs = 0;
        for (Membre membre : membres) {
            BigDecimal total = totalParMembre.getOrDefault(membre.getId(), BigDecimal.ZERO);
            long nb = nbParMembre.getOrDefault(membre.getId(), 0L);
            LocalDate dernier = dernierParMembre.get(membre.getId());
            lignes.add(new MembreRecouvrementDto(membre, total, nb, dernier));
            if (membre.getStatut() == StatutMembre.ACTIF) {
                nbActifs++;
            }
        }

        RecouvrementDto dto = new RecouvrementDto();
        dto.setMembres(lignes);
        dto.setTotalGeneral(cotisationRepository.totalGeneral());
        dto.setNombreMembres(membres.size());
        dto.setNombreMembresActifs(nbActifs);
        return dto;
    }

    private void appliquer(Membre membre, MembreDto dto) {
        StatutMembre statut = dto.getStatut() != null ? StatutMembre.valueOf(dto.getStatut()) : StatutMembre.ACTIF;
        membre.mettreAJour(
                dto.getNom().trim(),
                dto.getPrenoms(),
                dto.getMatricule(),
                dto.getEmail(),
                dto.getTelephone(),
                dto.getSpecialite(),
                statut,
                dto.getDateAdhesion()
        );
    }

    Membre trouver(Long id) {
        return membreRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Membre introuvable"));
    }
}
