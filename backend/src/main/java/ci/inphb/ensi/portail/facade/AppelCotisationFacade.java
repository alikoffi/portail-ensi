package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.AppelCotisation;
import ci.inphb.ensi.portail.domain.Membre;
import ci.inphb.ensi.portail.domain.PaiementCotisation;
import ci.inphb.ensi.portail.enums.StatutMembre;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.AppelCotisationDto;
import ci.inphb.ensi.portail.presentation.dto.AppelDetailDto;
import ci.inphb.ensi.portail.presentation.dto.LignePaiementDto;
import ci.inphb.ensi.portail.presentation.dto.PaiementDto;
import ci.inphb.ensi.portail.presentation.dto.StatistiqueCotisationDto;
import ci.inphb.ensi.portail.repository.AppelCotisationRepository;
import ci.inphb.ensi.portail.repository.MembreRepository;
import ci.inphb.ensi.portail.repository.PaiementCotisationRepository;
import ci.inphb.ensi.portail.service.MailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Appels de cotisation : creation (avec notification), saisie des paiements
 * par membre, suivi du recouvrement et statistiques.
 */
@Service
public class AppelCotisationFacade {

    private static final String[] MOIS_ABREGES = {
            "Janv.", "Févr.", "Mars", "Avr.", "Mai", "Juin",
            "Juil.", "Août", "Sept.", "Oct.", "Nov.", "Déc."
    };

    private final AppelCotisationRepository appelRepository;
    private final PaiementCotisationRepository paiementRepository;
    private final MembreRepository membreRepository;
    private final MailService mailService;

    public AppelCotisationFacade(AppelCotisationRepository appelRepository,
                                 PaiementCotisationRepository paiementRepository,
                                 MembreRepository membreRepository,
                                 MailService mailService) {
        this.appelRepository = appelRepository;
        this.paiementRepository = paiementRepository;
        this.membreRepository = membreRepository;
        this.mailService = mailService;
    }

    @Transactional(readOnly = true)
    public List<AppelCotisationDto> lister() {
        return appelRepository.findAllByOrderByDateButoirDesc().stream()
                .map(a -> new AppelCotisationDto(a,
                        paiementRepository.totalParAppel(a.getId()),
                        paiementRepository.nombrePayeursParAppel(a.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public AppelDetailDto detail(Long id) {
        AppelCotisation appel = trouver(id);
        Map<Long, PaiementCotisation> paiements = new HashMap<>();
        for (PaiementCotisation p : paiementRepository.findByAppelId(id)) {
            paiements.put(p.getMembre().getId(), p);
        }

        List<LignePaiementDto> lignes = new ArrayList<>();
        for (Membre membre : membreRepository.findAllByOrderByNomAscPrenomsAsc()) {
            PaiementCotisation p = paiements.get(membre.getId());
            // On ignore les membres inactifs qui n'ont jamais paye
            if (membre.getStatut() != StatutMembre.ACTIF && p == null) {
                continue;
            }
            BigDecimal paye = p != null ? p.getMontant() : BigDecimal.ZERO;
            BigDecimal reste = appel.getMontantAttendu().subtract(paye).max(BigDecimal.ZERO);

            LignePaiementDto ligne = new LignePaiementDto();
            ligne.setPaiementId(p != null ? p.getId() : null);
            ligne.setMembreId(membre.getId());
            ligne.setMembreNom(nomComplet(membre));
            ligne.setStatutMembre(membre.getStatut().name());
            ligne.setMontantAttendu(appel.getMontantAttendu());
            ligne.setMontantPaye(paye);
            ligne.setReste(reste);
            ligne.setAJour(paye.compareTo(appel.getMontantAttendu()) >= 0);
            ligne.setDatePaiement(p != null ? p.getDatePaiement() : null);
            ligne.setNote(p != null ? p.getNote() : null);
            lignes.add(ligne);
        }

        AppelCotisationDto dto = new AppelCotisationDto(appel,
                paiementRepository.totalParAppel(id), paiementRepository.nombrePayeursParAppel(id));
        return new AppelDetailDto(dto, lignes);
    }

    @Transactional
    public AppelCotisationDto enregistrer(AppelCotisationDto dto) {
        AppelCotisation appel = new AppelCotisation();
        appel.mettreAJour(dto.getLibelle().trim(), dto.getMontantAttendu(), dto.getDateButoir());
        appel.setDateCreation(LocalDate.now());
        AppelCotisation cree = appelRepository.save(appel);
        notifierMembres(cree);
        return new AppelCotisationDto(cree, BigDecimal.ZERO, 0);
    }

    @Transactional
    public AppelCotisationDto modifier(AppelCotisationDto dto) {
        if (dto.getId() == null) {
            throw PortailException.nonTrouve("Identifiant de l'appel manquant");
        }
        AppelCotisation appel = trouver(dto.getId());
        appel.mettreAJour(dto.getLibelle().trim(), dto.getMontantAttendu(), dto.getDateButoir());
        return new AppelCotisationDto(appel,
                paiementRepository.totalParAppel(appel.getId()), paiementRepository.nombrePayeursParAppel(appel.getId()));
    }

    @Transactional
    public void supprimer(Long id) {
        appelRepository.delete(trouver(id));
    }

    // ---------- Paiements ----------
    @Transactional
    public void enregistrerPaiement(PaiementDto dto) {
        AppelCotisation appel = trouver(dto.getAppelId());
        Membre membre = membreRepository.findById(dto.getMembreId())
                .orElseThrow(() -> PortailException.nonTrouve("Membre introuvable"));
        LocalDate date = dto.getDatePaiement() != null ? dto.getDatePaiement() : LocalDate.now();

        PaiementCotisation paiement = paiementRepository
                .findByAppelIdAndMembreId(appel.getId(), membre.getId())
                .orElseGet(() -> new PaiementCotisation(appel, membre));
        paiement.mettreAJour(dto.getMontant(), date, dto.getNote());
        paiementRepository.save(paiement);
    }

    @Transactional
    public void supprimerPaiement(Long appelId, Long membreId) {
        paiementRepository.findByAppelIdAndMembreId(appelId, membreId)
                .ifPresent(paiementRepository::delete);
    }

    // ---------- Statistiques (montant collecte par mois) ----------
    @Transactional(readOnly = true)
    public StatistiqueCotisationDto statistiques(Integer annee) {
        List<Integer> annees = paiementRepository.anneesDisponibles();
        int anneeCible = annee != null ? annee : (annees.isEmpty() ? Year.now().getValue() : annees.get(0));

        BigDecimal[] parMois = new BigDecimal[12];
        Arrays.fill(parMois, BigDecimal.ZERO);
        for (Object[] ligne : paiementRepository.totalParMoisPourAnnee(anneeCible)) {
            int mois = ((Number) ligne[0]).intValue();
            parMois[mois - 1] = (BigDecimal) ligne[1];
        }
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal m : parMois) {
            total = total.add(m);
        }

        StatistiqueCotisationDto dto = new StatistiqueCotisationDto();
        dto.setAnnee(anneeCible);
        dto.setAnneesDisponibles(annees);
        dto.setMoisLabels(List.of(MOIS_ABREGES));
        dto.setMontantsParMois(new ArrayList<>(Arrays.asList(parMois)));
        dto.setTotalAnnee(total);
        return dto;
    }

    private void notifierMembres(AppelCotisation appel) {
        membreRepository.findAllByOrderByNomAscPrenomsAsc().stream()
                .filter(m -> m.getStatut() == StatutMembre.ACTIF)
                .filter(m -> StringUtils.hasText(m.getEmail()))
                .forEach(m -> mailService.envoyerAppelCotisation(m, appel));
    }

    private String nomComplet(Membre m) {
        return (m.getNom() + " " + (m.getPrenoms() != null ? m.getPrenoms() : "")).trim();
    }

    private AppelCotisation trouver(Long id) {
        return appelRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Appel de cotisation introuvable"));
    }
}
