package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.PaiementCotisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaiementCotisationRepository extends JpaRepository<PaiementCotisation, Long> {

    List<PaiementCotisation> findByAppelId(Long appelId);

    Optional<PaiementCotisation> findByAppelIdAndMembreId(Long appelId, Long membreId);

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM PaiementCotisation p WHERE p.appel.id = :appelId")
    BigDecimal totalParAppel(@Param("appelId") Long appelId);

    @Query("SELECT COUNT(p) FROM PaiementCotisation p WHERE p.appel.id = :appelId")
    long nombrePayeursParAppel(@Param("appelId") Long appelId);

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM PaiementCotisation p")
    BigDecimal totalGeneral();

    /** {membreId, total, nombre, dernierPaiement} agrege par membre. */
    @Query("SELECT p.membre.id, SUM(p.montant), COUNT(p), MAX(p.datePaiement) FROM PaiementCotisation p GROUP BY p.membre.id")
    List<Object[]> agregatParMembre();

    /** {membreId, montant} pour un appel donne (calcul des retards). */
    @Query("SELECT p.membre.id, p.montant FROM PaiementCotisation p WHERE p.appel.id = :appelId")
    List<Object[]> montantsParMembrePourAppel(@Param("appelId") Long appelId);

    /** {mois (1-12), total} des paiements pour une annee (statistiques). */
    @Query("SELECT month(p.datePaiement), SUM(p.montant) FROM PaiementCotisation p "
            + "WHERE year(p.datePaiement) = :annee GROUP BY month(p.datePaiement)")
    List<Object[]> totalParMoisPourAnnee(@Param("annee") int annee);

    /** Annees distinctes presentes dans les paiements. */
    @Query("SELECT DISTINCT year(p.datePaiement) FROM PaiementCotisation p ORDER BY year(p.datePaiement) DESC")
    List<Integer> anneesDisponibles();
}
