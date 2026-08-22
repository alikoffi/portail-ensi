package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.Pv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PvRepository extends JpaRepository<Pv, Long> {

    /** Tous les proces-verbaux, du plus recent au plus ancien. */
    List<Pv> findAllByOrderByDatePvDesc();

    /** Idem, avec l'evenement rattache charge en une seule requete. */
    @Query("SELECT p FROM Pv p LEFT JOIN FETCH p.evenement ORDER BY p.datePv DESC, p.id DESC")
    List<Pv> listerAvecEvenement();

    /** Proces-verbal rattache a un evenement donne. */
    Optional<Pv> findByEvenementId(Long evenementId);

    /** Proces-verbaux rattaches a une liste d'evenements (apercu du planning). */
    List<Pv> findByEvenementIdIn(Collection<Long> evenementIds);
}
