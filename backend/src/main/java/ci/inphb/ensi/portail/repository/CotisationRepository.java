package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.Cotisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CotisationRepository extends JpaRepository<Cotisation, Long> {

    List<Cotisation> findByMembreIdOrderByDatePaiementDescIdDesc(Long membreId);

    @Query("SELECT COALESCE(SUM(c.montant), 0) FROM Cotisation c WHERE c.membre.id = :membreId")
    BigDecimal totalParMembre(@Param("membreId") Long membreId);

    @Query("SELECT COALESCE(SUM(c.montant), 0) FROM Cotisation c")
    BigDecimal totalGeneral();

    /** {membreId, total, nombre} agrege par membre. */
    @Query("SELECT c.membre.id, SUM(c.montant), COUNT(c) FROM Cotisation c GROUP BY c.membre.id")
    List<Object[]> agregatParMembre();

    /** Annees distinctes presentes dans les paiements, les plus recentes d'abord. */
    @Query("SELECT DISTINCT year(c.datePaiement) FROM Cotisation c ORDER BY year(c.datePaiement) DESC")
    List<Integer> anneesDisponibles();

    /** {mois (1-12), total} pour une annee donnee. */
    @Query("SELECT month(c.datePaiement), SUM(c.montant) FROM Cotisation c "
            + "WHERE year(c.datePaiement) = :annee GROUP BY month(c.datePaiement)")
    List<Object[]> totalParMoisPourAnnee(@Param("annee") int annee);
}
