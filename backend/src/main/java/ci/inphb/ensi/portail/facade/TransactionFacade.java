package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Transaction;
import ci.inphb.ensi.portail.enums.TypeTransaction;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.TransactionDto;
import ci.inphb.ensi.portail.repository.TransactionRepository;
import ci.inphb.ensi.portail.service.PdfService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestion des mouvements financiers.
 */
@Service
public class TransactionFacade {

    private final TransactionRepository transactionRepository;
    private final PdfService pdfService;

    public TransactionFacade(TransactionRepository transactionRepository, PdfService pdfService) {
        this.transactionRepository = transactionRepository;
        this.pdfService = pdfService;
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> lister() {
        return transactionRepository.findAllByOrderByDateTxDescIdDesc()
                .stream()
                .map(TransactionDto::new)
                .toList();
    }

    @Transactional
    public TransactionDto enregistrer(TransactionDto dto) {
        Transaction transaction = new Transaction();
        appliquer(transaction, dto);
        return new TransactionDto(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionDto modifier(TransactionDto dto) {
        if (dto.getId() == null) {
            throw PortailException.nonTrouve("Identifiant de la transaction manquant");
        }
        Transaction transaction = trouver(dto.getId());
        appliquer(transaction, dto);
        return new TransactionDto(transaction);
    }

    @Transactional
    public void supprimer(Long id) {
        transactionRepository.delete(trouver(id));
    }

    @Transactional(readOnly = true)
    public byte[] exporterPdf() {
        List<TransactionDto> transactions = lister();
        BigDecimal recettes = transactionRepository.totalParType(TypeTransaction.RECETTE);
        BigDecimal depenses = transactionRepository.totalParType(TypeTransaction.DEPENSE);

        Map<String, Object> variables = new HashMap<>();
        variables.put("transactions", transactions);
        variables.put("totalRecettes", recettes);
        variables.put("totalDepenses", depenses);
        variables.put("solde", recettes.subtract(depenses));
        variables.put("dateEdition", LocalDate.now());
        return pdfService.genererDepuisTemplate("pdf/journal", variables);
    }

    private void appliquer(Transaction transaction, TransactionDto dto) {
        transaction.mettreAJour(
                dto.getLibelle().trim(),
                TypeTransaction.valueOf(dto.getType()),
                dto.getMontant(),
                dto.getDateTx(),
                dto.getCategorie(),
                dto.getNote()
        );
    }

    private Transaction trouver(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Transaction introuvable"));
    }
}
