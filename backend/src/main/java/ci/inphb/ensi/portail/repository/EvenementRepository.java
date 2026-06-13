package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    /** Evenements a venir, du plus proche au plus lointain. */
    List<Evenement> findByDateEventGreaterThanEqualOrderByDateEventAsc(LocalDate date);

    /** Nombre d'evenements dans l'intervalle (mois courant). */
    @Query("SELECT COUNT(e) FROM Evenement e WHERE e.dateEvent BETWEEN :debut AND :fin")
    long compterEntre(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
