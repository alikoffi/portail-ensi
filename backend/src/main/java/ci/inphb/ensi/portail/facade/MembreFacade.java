package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.AppelCotisation;
import ci.inphb.ensi.portail.domain.Membre;
import ci.inphb.ensi.portail.enums.StatutMembre;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.MembreDto;
import ci.inphb.ensi.portail.presentation.dto.MembreRecouvrementDto;
import ci.inphb.ensi.portail.presentation.dto.RecouvrementDto;
import ci.inphb.ensi.portail.repository.AppelCotisationRepository;
import ci.inphb.ensi.portail.repository.MembreRepository;
import ci.inphb.ensi.portail.repository.PaiementCotisationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestion des membres de la promotion et du recouvrement des cotisations
 * (base sur les appels de cotisation et leurs paiements).
 */
@Service
public class MembreFacade {

    private final MembreRepository membreRepository;
    private final PaiementCotisationRepository paiementRepository;
    private final AppelCotisationRepository appelRepository;

    public MembreFacade(MembreRepository membreRepository,
                        PaiementCotisationRepository paiementRepository,
                        AppelCotisationRepository appelRepository) {
        this.membreRepository = membreRepository;
        this.paiementRepository = paiementRepository;
        this.appelRepository = appelRepository;
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
        // Agregats de paiement par membre
        Map<Long, BigDecimal> totalParMembre = new HashMap<>();
        Map<Long, Long> nbParMembre = new HashMap<>();
        Map<Long, LocalDate> dernierParMembre = new HashMap<>();
        for (Object[] ligne : paiementRepository.agregatParMembre()) {
            Long membreId = (Long) ligne[0];
            totalParMembre.put(membreId, (BigDecimal) ligne[1]);
            nbParMembre.put(membreId, (Long) ligne[2]);
            dernierParMembre.put(membreId, (LocalDate) ligne[3]);
        }

        // Retard : appels echus -> montant paye par membre
        List<AppelCotisation> appelsEchus = appelRepository.findByDateButoirBefore(LocalDate.now());
        Map<Long, int[]> nbRetardParMembre = new HashMap<>();           // membreId -> [nbAppels]
        Map<Long, BigDecimal> resteParMembre = new HashMap<>();
        for (AppelCotisation appel : appelsEchus) {
            Map<Long, BigDecimal> payeParMembre = new HashMap<>();
            for (Object[] ligne : paiementRepository.montantsParMembrePourAppel(appel.getId())) {
                payeParMembre.put((Long) ligne[0], (BigDecimal) ligne[1]);
            }
            for (Membre membre : membreRepository.findAllByOrderByNomAscPrenomsAsc()) {
                if (membre.getStatut() != StatutMembre.ACTIF) {
                    continue;
                }
                BigDecimal paye = payeParMembre.getOrDefault(membre.getId(), BigDecimal.ZERO);
                BigDecimal reste = appel.getMontantAttendu().subtract(paye);
                if (reste.signum() > 0) {
                    nbRetardParMembre.computeIfAbsent(membre.getId(), k -> new int[]{0})[0]++;
                    resteParMembre.merge(membre.getId(), reste, BigDecimal::add);
                }
            }
        }

        List<Membre> membres = membreRepository.findAllByOrderByNomAscPrenomsAsc();
        List<MembreRecouvrementDto> lignes = new ArrayList<>();
        long nbActifs = 0;
        for (Membre membre : membres) {
            BigDecimal total = totalParMembre.getOrDefault(membre.getId(), BigDecimal.ZERO);
            long nb = nbParMembre.getOrDefault(membre.getId(), 0L);
            LocalDate dernier = dernierParMembre.get(membre.getId());
            int appelsRetard = nbRetardParMembre.containsKey(membre.getId()) ? nbRetardParMembre.get(membre.getId())[0] : 0;
            BigDecimal reste = resteParMembre.getOrDefault(membre.getId(), BigDecimal.ZERO);
            lignes.add(new MembreRecouvrementDto(membre, total, nb, dernier, appelsRetard, reste));
            if (membre.getStatut() == StatutMembre.ACTIF) {
                nbActifs++;
            }
        }

        RecouvrementDto dto = new RecouvrementDto();
        dto.setMembres(lignes);
        dto.setTotalGeneral(paiementRepository.totalGeneral());
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
