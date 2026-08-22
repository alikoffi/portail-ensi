package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Transaction;
import ci.inphb.ensi.portail.enums.TypeTransaction;
import ci.inphb.ensi.portail.presentation.dto.EvenementDto;
import ci.inphb.ensi.portail.presentation.dto.StatistiqueDto;
import ci.inphb.ensi.portail.presentation.dto.TableauBordDto;
import ci.inphb.ensi.portail.presentation.dto.TransactionDto;
import ci.inphb.ensi.portail.repository.DocumentRepository;
import ci.inphb.ensi.portail.repository.EvenementRepository;
import ci.inphb.ensi.portail.repository.PvRepository;
import ci.inphb.ensi.portail.repository.TransactionRepository;
import ci.inphb.ensi.portail.utils.EvenementEnrichissement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Construit la synthese du tableau de bord (indicateurs financiers + apercu agenda).
 */
@Service
public class TableauBordFacade {

    private static final int NB_EVENEMENTS_APERCU = 2;

    private final TransactionRepository transactionRepository;
    private final EvenementRepository evenementRepository;
    private final PvRepository pvRepository;
    private final DocumentRepository documentRepository;

    public TableauBordFacade(TransactionRepository transactionRepository,
                             EvenementRepository evenementRepository,
                             PvRepository pvRepository,
                             DocumentRepository documentRepository) {
        this.transactionRepository = transactionRepository;
        this.evenementRepository = evenementRepository;
        this.pvRepository = pvRepository;
        this.documentRepository = documentRepository;
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
                .limit(NB_EVENEMENTS_APERCU)
                .map(EvenementDto::new)
                .toList();

        List<EvenementDto> derniers = evenementRepository
                .findByDateEventLessThanOrderByDateEventDescIdDesc(aujourdHui)
                .stream()
                .limit(NB_EVENEMENTS_APERCU)
                .map(EvenementDto::new)
                .toList();

        // procès-verbal et pièces jointes, pour l'action « voir le PV » du tableau de bord
        EvenementEnrichissement.appliquer(prochains, pvRepository, documentRepository);
        EvenementEnrichissement.appliquer(derniers, pvRepository, documentRepository);

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
        dto.setDerniersEvenements(derniers);
        dto.setDernieresTransactions(dernieres);
        return dto;
    }

    @Transactional(readOnly = true)
    public StatistiqueDto statistiques() {
        StatistiqueDto dto = new StatistiqueDto();
        construireEvolutionSolde(dto);
        construireDepensesParCategorie(dto);
        return dto;
    }

    /** Solde cumule mois par mois. */
    private void construireEvolutionSolde(StatistiqueDto dto) {
        DateTimeFormatter formatMois = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH);
        Map<YearMonth, BigDecimal> netParMois = new LinkedHashMap<>();

        for (Transaction t : transactionRepository.findAllByOrderByDateTxAscIdAsc()) {
            BigDecimal signe = t.getType() == TypeTransaction.RECETTE ? t.getMontant() : t.getMontant().negate();
            YearMonth mois = YearMonth.from(t.getDateTx());
            netParMois.merge(mois, signe, BigDecimal::add);
        }

        List<String> labels = new ArrayList<>();
        List<BigDecimal> cumul = new ArrayList<>();
        BigDecimal courant = BigDecimal.ZERO;
        for (Map.Entry<YearMonth, BigDecimal> entree : netParMois.entrySet()) {
            courant = courant.add(entree.getValue());
            labels.add(entree.getKey().atDay(1).format(formatMois));
            cumul.add(courant);
        }
        dto.setMoisLabels(labels);
        dto.setSoldeCumule(cumul);
    }

    /** Repartition des depenses par categorie. */
    private void construireDepensesParCategorie(StatistiqueDto dto) {
        List<String> categories = new ArrayList<>();
        List<BigDecimal> montants = new ArrayList<>();
        for (Object[] ligne : transactionRepository.totalParCategorie(TypeTransaction.DEPENSE)) {
            categories.add(ligne[0] != null ? (String) ligne[0] : "Non catégorisé");
            montants.add((BigDecimal) ligne[1]);
        }
        dto.setCategoriesDepenses(categories);
        dto.setMontantsDepenses(montants);
    }
}
