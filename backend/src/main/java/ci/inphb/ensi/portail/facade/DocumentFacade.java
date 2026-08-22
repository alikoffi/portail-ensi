package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Document;
import ci.inphb.ensi.portail.domain.Evenement;
import ci.inphb.ensi.portail.domain.Pv;
import ci.inphb.ensi.portail.enums.CategorieDocument;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.DocumentDto;
import ci.inphb.ensi.portail.repository.DocumentRepository;
import ci.inphb.ensi.portail.repository.EvenementRepository;
import ci.inphb.ensi.portail.repository.PvRepository;
import ci.inphb.ensi.portail.service.storage.FichierStorage;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Televersement, consultation et suppression des pieces jointes rattachees
 * aux proces-verbaux et aux evenements.
 */
@Service
public class DocumentFacade {

    private final DocumentRepository documentRepository;
    private final PvRepository pvRepository;
    private final EvenementRepository evenementRepository;
    private final FichierStorage fichierStorage;

    public DocumentFacade(DocumentRepository documentRepository,
                          PvRepository pvRepository,
                          EvenementRepository evenementRepository,
                          FichierStorage fichierStorage) {
        this.documentRepository = documentRepository;
        this.pvRepository = pvRepository;
        this.evenementRepository = evenementRepository;
        this.fichierStorage = fichierStorage;
    }

    /** Fichier pret a etre renvoye au navigateur. */
    public record FichierTelecharge(byte[] contenu, MediaType mediaType, String nomOriginal) {
    }

    @Transactional
    public DocumentDto televerser(MultipartFile fichier, Long pvId, Long evenementId,
                                  String libelle, boolean principal) {
        if ((pvId == null) == (evenementId == null)) {
            throw PortailException.requeteInvalide(
                    "Le document doit être rattaché soit à un procès-verbal, soit à un évènement.");
        }

        Document document = new Document();
        CategorieDocument categorie;
        if (pvId != null) {
            Pv pv = pvRepository.findById(pvId)
                    .orElseThrow(() -> PortailException.nonTrouve("Procès-verbal introuvable"));
            document.setPv(pv);
            categorie = CategorieDocument.PV_DOCUMENT;
        } else {
            Evenement evenement = evenementRepository.findById(evenementId)
                    .orElseThrow(() -> PortailException.nonTrouve("Évènement introuvable"));
            document.setEvenement(evenement);
            categorie = CategorieDocument.EVENEMENT_DOCUMENT;
        }

        FichierStorage.FichierStocke stocke = fichierStorage.enregistrer(fichier, categorie);
        document.referencer(stocke.dossier(), stocke.nomStocke(), stocke.nomOriginal(),
                stocke.contentType(), stocke.taille(), fichierStorage.typeStockage());
        document.setLibelle(libelle != null && !libelle.isBlank() ? libelle.trim() : null);
        document.setPrincipal(principal);

        try {
            if (principal) {
                retirerAncienPrincipal(pvId, evenementId);
            }
            return new DocumentDto(documentRepository.save(document));
        } catch (RuntimeException exception) {
            // le fichier est deja ecrit : on evite de laisser un orphelin dans le stockage
            fichierStorage.supprimer(stocke.dossier(), stocke.nomStocke());
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<DocumentDto> listerParPv(Long pvId) {
        return documentRepository.findByPvIdOrderByPrincipalDescIdAsc(pvId)
                .stream()
                .map(DocumentDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentDto> listerParEvenement(Long evenementId) {
        return documentRepository.findByEvenementIdOrderByPrincipalDescIdAsc(evenementId)
                .stream()
                .map(DocumentDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public FichierTelecharge contenu(Long id) {
        Document document = trouver(id);
        FichierStorage.ContenuFichier contenu = fichierStorage.lire(document.getDossier(), document.getNomStocke());
        return new FichierTelecharge(contenu.contenu(), contenu.mediaType(), document.getNomOriginal());
    }

    @Transactional
    public void supprimer(Long id) {
        Document document = trouver(id);
        String dossier = document.getDossier();
        String nomStocke = document.getNomStocke();
        documentRepository.delete(document);
        fichierStorage.supprimer(dossier, nomStocke);
    }

    /** Un seul document principal par rattachement. */
    private void retirerAncienPrincipal(Long pvId, Long evenementId) {
        List<Document> existants = pvId != null
                ? documentRepository.findByPvIdOrderByPrincipalDescIdAsc(pvId)
                : documentRepository.findByEvenementIdOrderByPrincipalDescIdAsc(evenementId);
        for (Document existant : existants) {
            existant.setPrincipal(false);
        }
    }

    private Document trouver(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Document introuvable"));
    }
}
