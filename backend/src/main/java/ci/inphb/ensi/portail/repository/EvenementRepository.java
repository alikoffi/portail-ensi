package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    /** Tous les evenements, du plus recent au plus ancien. */
    List<Evenement> findAllByOrderByDateEventDesc();

    /** Evenements a venir, du plus proche au plus lointain. */
    List<Evenement> findByDateEventGreaterThanEqualOrderByDateEventAsc(LocalDate date);

    /** Evenements passes, du plus recent au plus ancien. */
    List<Evenement> findByDateEventLessThanOrderByDateEventDescIdDesc(LocalDate date);

    /** Evenements dans un intervalle (rappels). */
    List<Evenement> findByDateEventBetweenOrderByDateEventAsc(LocalDate debut, LocalDate fin);

    /** Nombre d'evenements dans l'intervalle (mois courant). */
    @Query("SELECT COUNT(e) FROM Evenement e WHERE e.dateEvent BETWEEN :debut AND :fin")
    long compterEntre(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
