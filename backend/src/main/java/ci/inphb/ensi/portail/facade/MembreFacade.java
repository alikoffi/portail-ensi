package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Membre;
import ci.inphb.ensi.portail.enums.StatutMembre;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.MembreDto;
import ci.inphb.ensi.portail.presentation.dto.MembreRecouvrementDto;
import ci.inphb.ensi.portail.presentation.dto.RecouvrementDto;
import ci.inphb.ensi.portail.repository.CotisationRepository;
import ci.inphb.ensi.portail.repository.MembreRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Gestion des membres de la promotion et du recouvrement des cotisations.
 */
@Service
public class MembreFacade {

    /** Format des periodes de cotisation (mois). */
    private static final DateTimeFormatter FORMAT_PERIODE = DateTimeFormatter.ofPattern("yyyy-MM");

    private final MembreRepository membreRepository;
    private final CotisationRepository cotisationRepository;

    /** Mois de depart commun (AAAA-MM) si la date d'adhesion n'est pas renseignee. */
    @Value("${app.cotisation.mois-debut:}")
    private String moisDebutCommun;

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

        // Periodes (mois) payees par membre
        Map<Long, Set<String>> periodesParMembre = new HashMap<>();
        for (Object[] ligne : cotisationRepository.periodesParMembre()) {
            periodesParMembre
                    .computeIfAbsent((Long) ligne[0], k -> new HashSet<>())
                    .add((String) ligne[1]);
        }

        YearMonth dernierMoisEchu = YearMonth.now().minusMonths(1);

        List<Membre> membres = membreRepository.findAllByOrderByNomAscPrenomsAsc();
        List<MembreRecouvrementDto> lignes = new ArrayList<>();
        long nbActifs = 0;
        for (Membre membre : membres) {
            BigDecimal total = totalParMembre.getOrDefault(membre.getId(), BigDecimal.ZERO);
            long nb = nbParMembre.getOrDefault(membre.getId(), 0L);
            LocalDate dernier = dernierParMembre.get(membre.getId());
            int retard = calculerMoisEnRetard(membre, periodesParMembre.getOrDefault(membre.getId(), Set.of()), dernierMoisEchu);
            lignes.add(new MembreRecouvrementDto(membre, total, nb, dernier, retard));
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

    /**
     * Nombre de mois echus (termines) non payes pour un membre ACTIF,
     * a partir de son mois d'adhesion (ou du mois de depart commun si absent).
     */
    private int calculerMoisEnRetard(Membre membre, Set<String> periodesPayees, YearMonth dernierMoisEchu) {
        if (membre.getStatut() != StatutMembre.ACTIF) {
            return 0;
        }
        YearMonth debut = moisDepart(membre);
        if (debut == null || debut.isAfter(dernierMoisEchu)) {
            return 0;
        }
        int retard = 0;
        for (YearMonth mois = debut; !mois.isAfter(dernierMoisEchu); mois = mois.plusMonths(1)) {
            if (!periodesPayees.contains(mois.format(FORMAT_PERIODE))) {
                retard++;
            }
        }
        return retard;
    }

    private YearMonth moisDepart(Membre membre) {
        if (membre.getDateAdhesion() != null) {
            return YearMonth.from(membre.getDateAdhesion());
        }
        if (StringUtils.hasText(moisDebutCommun)) {
            try {
                return YearMonth.parse(moisDebutCommun.trim());
            } catch (Exception ignore) {
                return null;
            }
        }
        return null;
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
