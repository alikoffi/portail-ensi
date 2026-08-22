package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Document;
import ci.inphb.ensi.portail.domain.Evenement;
import ci.inphb.ensi.portail.domain.Pv;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.PvDto;
import ci.inphb.ensi.portail.repository.DocumentRepository;
import ci.inphb.ensi.portail.repository.EvenementRepository;
import ci.inphb.ensi.portail.repository.PvRepository;
import ci.inphb.ensi.portail.service.PdfService;
import ci.inphb.ensi.portail.service.storage.FichierStorage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Gestion des proces-verbaux : contenu redige, evenement rattache et pieces
 * jointes televerses.
 */
@Service
public class PvFacade {

    private final PvRepository pvRepository;
    private final EvenementRepository evenementRepository;
    private final DocumentRepository documentRepository;
    private final FichierStorage fichierStorage;
    private final PdfService pdfService;

    public PvFacade(PvRepository pvRepository,
                    EvenementRepository evenementRepository,
                    DocumentRepository documentRepository,
                    FichierStorage fichierStorage,
                    PdfService pdfService) {
        this.pvRepository = pvRepository;
        this.evenementRepository = evenementRepository;
        this.documentRepository = documentRepository;
        this.fichierStorage = fichierStorage;
        this.pdfService = pdfService;
    }

    @Transactional(readOnly = true)
    public List<PvDto> lister() {
        List<Pv> pvs = pvRepository.listerAvecEvenement();
        List<PvDto> dtos = pvs.stream().map(PvDto::new).toList();
        appliquerNbDocuments(dtos);
        return dtos;
    }

    @Transactional(readOnly = true)
    public PvDto detail(Long id) {
        PvDto dto = new PvDto(trouver(id));
        appliquerNbDocuments(List.of(dto));
        return dto;
    }

    @Transactional
    public PvDto enregistrer(PvDto dto) {
        Pv pv = new Pv();
        appliquer(pv, dto);
        return new PvDto(pvRepository.save(pv));
    }

    @Transactional
    public PvDto modifier(PvDto dto) {
        if (dto.getId() == null) {
            throw PortailException.nonTrouve("Identifiant du procès-verbal manquant");
        }
        Pv pv = trouver(dto.getId());
        appliquer(pv, dto);
        PvDto resultat = new PvDto(pv);
        appliquerNbDocuments(List.of(resultat));
        return resultat;
    }

    @Transactional
    public void supprimer(Long id) {
        Pv pv = trouver(id);
        // les lignes "document" partent en cascade cote base : on nettoie les fichiers
        List<Document> documents = documentRepository.findByPvIdOrderByPrincipalDescIdAsc(id);
        pvRepository.delete(pv);
        pvRepository.flush();
        for (Document document : documents) {
            fichierStorage.supprimer(document.getDossier(), document.getNomStocke());
        }
    }

    @Transactional(readOnly = true)
    public byte[] exporterPdf(Long id) {
        PvDto pv = new PvDto(trouver(id));
        Map<String, Object> variables = new HashMap<>();
        variables.put("pv", pv);
        variables.put("dateEdition", LocalDate.now());
        return pdfService.genererDepuisTemplate("pdf/pv", variables);
    }

    private void appliquer(Pv pv, PvDto dto) {
        pv.mettreAJour(
                dto.getObjet().trim(),
                dto.getDatePv(),
                dto.getLieu(),
                dto.getPresents(),
                dto.getOrdreDuJour(),
                dto.getDecisions(),
                dto.getSignataires()
        );
        pv.setEvenement(resoudreEvenement(pv, dto.getEvenementId()));
    }

    /** Verifie qu'un evenement ne porte pas deja un autre proces-verbal. */
    private Evenement resoudreEvenement(Pv pv, Long evenementId) {
        if (evenementId == null) {
            return null;
        }
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> PortailException.nonTrouve("Évènement introuvable"));
        pvRepository.findByEvenementId(evenementId).ifPresent(existant -> {
            if (!existant.getId().equals(pv.getId())) {
                throw PortailException.conflit(
                        "Cet évènement est déjà rattaché au procès-verbal « " + existant.getObjet() + " ».");
            }
        });
        return evenement;
    }

    /** Compte les pieces jointes de plusieurs PV en une seule requete. */
    private void appliquerNbDocuments(List<PvDto> dtos) {
        List<Long> ids = dtos.stream().map(PvDto::getId).filter(Objects::nonNull).toList();
        if (ids.isEmpty()) {
            return;
        }
        Map<Long, Long> compteurs = new HashMap<>();
        for (Object[] ligne : documentRepository.compterParPv(ids)) {
            compteurs.put((Long) ligne[0], (Long) ligne[1]);
        }
        for (PvDto dto : dtos) {
            dto.setNbDocuments(compteurs.getOrDefault(dto.getId(), 0L));
        }
    }

    private Pv trouver(Long id) {
        return pvRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Procès-verbal introuvable"));
    }
}
