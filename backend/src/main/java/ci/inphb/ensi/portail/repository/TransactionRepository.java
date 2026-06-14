package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.Transaction;
import ci.inphb.ensi.portail.enums.TypeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /** Total des montants pour un type donne (0 si aucun). */
    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM Transaction t WHERE t.type = :type")
    BigDecimal totalParType(@Param("type") TypeTransaction type);

    /** Dernieres transactions, les plus recentes d'abord. */
    List<Transaction> findTop4ByOrderByDateTxDescIdDesc();

    /** Toutes les transactions, les plus recentes d'abord. */
    List<Transaction> findAllByOrderByDateTxDescIdDesc();

    /** Toutes les transactions, les plus anciennes d'abord (pour l'evolution). */
    List<Transaction> findAllByOrderByDateTxAscIdAsc();

    /** Total des depenses par categorie, du plus eleve au plus faible. */
    @Query("SELECT t.categorie, SUM(t.montant) FROM Transaction t "
            + "WHERE t.type = :type GROUP BY t.categorie ORDER BY SUM(t.montant) DESC")
    List<Object[]> totalParCategorie(@Param("type") TypeTransaction type);
}
