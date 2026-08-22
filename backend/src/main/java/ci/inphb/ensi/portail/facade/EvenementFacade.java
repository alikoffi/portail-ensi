package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Document;
import ci.inphb.ensi.portail.domain.Evenement;
import ci.inphb.ensi.portail.enums.StatutEvenement;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.EvenementDto;
import ci.inphb.ensi.portail.repository.DocumentRepository;
import ci.inphb.ensi.portail.repository.EvenementRepository;
import ci.inphb.ensi.portail.repository.PvRepository;
import ci.inphb.ensi.portail.service.storage.FichierStorage;
import ci.inphb.ensi.portail.utils.EvenementEnrichissement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gestion des evenements du planning : contenu, statut d'avancement et
 * proces-verbal rattache.
 */
@Service
public class EvenementFacade {

    private final EvenementRepository evenementRepository;
    private final PvRepository pvRepository;
    private final DocumentRepository documentRepository;
    private final FichierStorage fichierStorage;

    public EvenementFacade(EvenementRepository evenementRepository,
                           PvRepository pvRepository,
                           DocumentRepository documentRepository,
                           FichierStorage fichierStorage) {
        this.evenementRepository = evenementRepository;
        this.pvRepository = pvRepository;
        this.documentRepository = documentRepository;
        this.fichierStorage = fichierStorage;
    }

    @Transactional(readOnly = true)
    public List<EvenementDto> lister() {
        List<EvenementDto> dtos = evenementRepository.findAllByOrderByDateEventDesc()
                .stream()
                .map(EvenementDto::new)
                .toList();
        EvenementEnrichissement.appliquer(dtos, pvRepository, documentRepository);
        return dtos;
    }

    @Transactional(readOnly = true)
    public EvenementDto detail(Long id) {
        EvenementDto dto = new EvenementDto(trouver(id));
        EvenementEnrichissement.appliquer(List.of(dto), pvRepository, documentRepository);
        return dto;
    }

    @Transactional
    public EvenementDto enregistrer(EvenementDto dto) {
        Evenement evenement = new Evenement();
        appliquer(evenement, dto);
        return new EvenementDto(evenementRepository.save(evenement));
    }

    @Transactional
    public EvenementDto modifier(EvenementDto dto) {
        if (dto.getId() == null) {
            throw PortailException.nonTrouve("Identifiant de l'évènement manquant");
        }
        Evenement evenement = trouver(dto.getId());
        appliquer(evenement, dto);
        return detailDe(evenement);
    }

    /** Cloture, annule ou reporte un evenement depuis le planning. */
    @Transactional
    public EvenementDto changerStatut(Long id, StatutEvenement statut) {
        if (statut == null) {
            throw PortailException.requeteInvalide("Le statut est obligatoire.");
        }
        Evenement evenement = trouver(id);
        evenement.setStatut(statut);
        return detailDe(evenement);
    }

    @Transactional
    public void supprimer(Long id) {
        Evenement evenement = trouver(id);
        // les lignes "document" partent en cascade cote base : on nettoie les fichiers
        List<Document> documents = documentRepository.findByEvenementIdOrderByPrincipalDescIdAsc(id);
        evenementRepository.delete(evenement);
        evenementRepository.flush();
        for (Document document : documents) {
            fichierStorage.supprimer(document.getDossier(), document.getNomStocke());
        }
    }

    private void appliquer(Evenement evenement, EvenementDto dto) {
        evenement.mettreAJour(
                dto.getNom().trim(),
                dto.getDateEvent(),
                dto.getHeure(),
                dto.getType(),
                dto.getLieu(),
                dto.getDescription(),
                dto.getStatut()
        );
    }

    private EvenementDto detailDe(Evenement evenement) {
        EvenementDto dto = new EvenementDto(evenement);
        EvenementEnrichissement.appliquer(List.of(dto), pvRepository, documentRepository);
        return dto;
    }

    private Evenement trouver(Long id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Évènement introuvable"));
    }
}
