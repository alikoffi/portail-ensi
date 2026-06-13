package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.enums.TypeTransaction;
import ci.inphb.ensi.portail.presentation.dto.EvenementDto;
import ci.inphb.ensi.portail.presentation.dto.TableauBordDto;
import ci.inphb.ensi.portail.presentation.dto.TransactionDto;
import ci.inphb.ensi.portail.repository.EvenementRepository;
import ci.inphb.ensi.portail.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Construit la synthese du tableau de bord (indicateurs financiers + apercu agenda).
 */
@Service
public class TableauBordFacade {

    private static final int NB_PROCHAINS_EVENEMENTS = 3;

    private final TransactionRepository transactionRepository;
    private final EvenementRepository evenementRepository;

    public TableauBordFacade(TransactionRepository transactionRepository,
                             EvenementRepository evenementRepository) {
        this.transactionRepository = transactionRepository;
        this.evenementRepository = evenementRepository;
    }

    @Transactional(readOnly = true)
    public TableauBordDto resume() {
        BigDecimal recettes = transactionRepository.totalParType(TypeTransaction.RECETTE);
        BigDecimal depenses = transactionRepository.totalParType(TypeTransaction.DEPENSE);

        LocalDate aujourdHui = LocalDate.now();
        LocalDate debutMois = aujourdHui.withDayOfMonth(1);
        LocalDate finMois = aujourdHui.withDayOfMonth(aujourdHui.lengthOfMonth());

        List<EvenementDto> prochains = evenementRepository
                .findByDateEventGreaterThanEqualOrderByDateEventAsc(aujourdHui)
                .stream()
                .limit(NB_PROCHAINS_EVENEMENTS)
                .map(EvenementDto::new)
                .toList();

        List<TransactionDto> dernieres = transactionRepository
                .findTop4ByOrderByDateTxDescIdDesc()
                .stream()
                .map(TransactionDto::new)
                .toList();

        TableauBordDto dto = new TableauBordDto();
        dto.setRecettesTotales(recettes);
        dto.setDepensesTotales(depenses);
        dto.setSolde(recettes.subtract(depenses));
        dto.setEvenementsCeMois(evenementRepository.compterEntre(debutMois, finMois));
        dto.setProchainsEvenements(prochains);
        dto.setDernieresTransactions(dernieres);
        return dto;
    }
}
