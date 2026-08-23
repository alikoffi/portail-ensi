package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Ecran;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.EcranDto;
import ci.inphb.ensi.portail.repository.EcranRepository;
import ci.inphb.ensi.portail.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Visibilite des ecrans. Le masquage decide par l'administrateur s'ajoute au
 * controle par role : un ecran masque disparait pour tout le monde, y compris
 * pour ceux que leur role autorisait a le voir.
 */
@Service
public class EcranFacade {

    private final EcranRepository ecranRepository;

    /**
     * Prefixes d'API des ecrans masques, tenus en memoire : cette liste est lue
     * a chaque requete HTTP, elle ne peut pas coûter un acces base a chaque fois.
     * Elle est recalculee a chaque changement de visibilite.
     */
    private volatile List<String> prefixesMasques = List.of();

    public EcranFacade(EcranRepository ecranRepository) {
        this.ecranRepository = ecranRepository;
    }

    @Transactional(readOnly = true)
    public List<EcranDto> lister() {
        return ecranRepository.findAllByOrderByOrdreAsc().stream().map(EcranDto::new).toList();
    }

    /**
     * Ce que le menu de l'utilisateur doit afficher.
     *
     * L'administrateur conserve tous les ecrans : le masquage vise les autres
     * roles, pas celui qui l'a decide. Le champ {@code visible} lui permet de
     * signaler dans son menu ce qui est retire aux autres.
     */
    @Transactional(readOnly = true)
    public List<EcranDto> listerVisibles() {
        boolean administrateur = SecurityUtils.estAdministrateur();
        return ecranRepository.findAllByOrderByOrdreAsc().stream()
                .filter(ecran -> administrateur || ecran.isVisible())
                .map(EcranDto::new)
                .toList();
    }

    @Transactional
    public EcranDto changerVisibilite(String code, boolean visible) {
        Ecran ecran = ecranRepository.findByCode(code)
                .orElseThrow(() -> PortailException.nonTrouve("Écran introuvable : " + code));

        if (ecran.isVerrouille() && !visible) {
            throw PortailException.requeteInvalide(
                    "« " + ecran.getLibelle() + " » ne peut pas être masqué : sans lui, plus aucun moyen "
                            + "de rétablir les autres écrans.");
        }

        ecran.setVisible(visible);
        ecranRepository.save(ecran);
        rafraichirPrefixesMasques();
        return new EcranDto(ecran);
    }

    /** Consultee par le filtre HTTP a chaque appel : jamais d'acces base ici. */
    public List<String> prefixesMasques() {
        return prefixesMasques;
    }

    @Transactional(readOnly = true)
    public void rafraichirPrefixesMasques() {
        this.prefixesMasques = ecranRepository.findByVisibleFalse().stream()
                .flatMap(e -> e.prefixes().stream())
                .toList();
    }
}
