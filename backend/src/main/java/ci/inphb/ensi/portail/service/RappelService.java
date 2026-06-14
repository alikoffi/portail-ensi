package ci.inphb.ensi.portail.service;

import ci.inphb.ensi.portail.domain.Utilisateur;
import ci.inphb.ensi.portail.enums.RoleUtilisateur;
import ci.inphb.ensi.portail.facade.MembreFacade;
import ci.inphb.ensi.portail.presentation.dto.EvenementDto;
import ci.inphb.ensi.portail.presentation.dto.MembreRecouvrementDto;
import ci.inphb.ensi.portail.presentation.dto.RappelResultatDto;
import ci.inphb.ensi.portail.repository.EvenementRepository;
import ci.inphb.ensi.portail.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Rappels automatiques : evenements a venir et cotisations en retard,
 * envoyes par email aux membres du bureau (roles non visiteurs).
 */
@Service
public class RappelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RappelService.class);

    private final EvenementRepository evenementRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MembreFacade membreFacade;
    private final MailService mailService;

    @Value("${app.rappel.jours-evenement:3}")
    private int joursEvenement;

    @Value("${app.rappel.jours-retard-cotisation:30}")
    private int joursRetardCotisation;

    public RappelService(EvenementRepository evenementRepository,
                         UtilisateurRepository utilisateurRepository,
                         MembreFacade membreFacade,
                         MailService mailService) {
        this.evenementRepository = evenementRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.membreFacade = membreFacade;
        this.mailService = mailService;
    }

    /** Declenchement quotidien (8h00 par defaut, configurable). */
    @Scheduled(cron = "${app.rappel.cron:0 0 8 * * *}")
    public void rappelQuotidien() {
        RappelResultatDto resultat = envoyerRappels();
        LOGGER.info("Rappel quotidien : {} evenement(s), {} membre(s) en retard, {} destinataire(s)",
                resultat.getNombreEvenements(), resultat.getNombreMembresEnRetard(), resultat.getNombreDestinataires());
    }

    @Transactional(readOnly = true)
    public RappelResultatDto envoyerRappels() {
        LocalDate aujourdHui = LocalDate.now();

        List<EvenementDto> evenements = evenementRepository
                .findByDateEventBetweenOrderByDateEventAsc(aujourdHui, aujourdHui.plusDays(joursEvenement))
                .stream().map(EvenementDto::new).toList();

        LocalDate limite = aujourdHui.minusDays(joursRetardCotisation);
        List<String> membresEnRetard = membreFacade.recouvrement().getMembres().stream()
                .filter(m -> "ACTIF".equals(m.getStatut()))
                .filter(m -> m.getDernierPaiement() == null || m.getDernierPaiement().isBefore(limite))
                .map(this::nomComplet)
                .toList();

        List<Utilisateur> destinataires = utilisateurRepository.findByActifTrueAndEmailIsNotNull().stream()
                .filter(u -> u.getRole() != RoleUtilisateur.VIEWER)
                .toList();

        if (!evenements.isEmpty() || !membresEnRetard.isEmpty()) {
            destinataires.forEach(u -> mailService.envoyerRappel(u, evenements, membresEnRetard));
        }
        return new RappelResultatDto(evenements.size(), membresEnRetard.size(), destinataires.size());
    }

    private String nomComplet(MembreRecouvrementDto m) {
        return (m.getNom() + " " + (m.getPrenoms() != null ? m.getPrenoms() : "")).trim();
    }
}
