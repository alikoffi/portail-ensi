package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    /** Pieces jointes d'un proces-verbal, le document principal en tete. */
    List<Document> findByPvIdOrderByPrincipalDescIdAsc(Long pvId);

    /** Pieces jointes d'un evenement. */
    List<Document> findByEvenementIdOrderByPrincipalDescIdAsc(Long evenementId);

    /** Nombre de pieces jointes par proces-verbal (evite un appel par PV). */
    @Query("SELECT d.pv.id, COUNT(d) FROM Document d WHERE d.pv.id IN :pvIds GROUP BY d.pv.id")
    List<Object[]> compterParPv(@Param("pvIds") Collection<Long> pvIds);

    /** Nombre de pieces jointes par evenement. */
    @Query("SELECT d.evenement.id, COUNT(d) FROM Document d WHERE d.evenement.id IN :evenementIds GROUP BY d.evenement.id")
    List<Object[]> compterParEvenement(@Param("evenementIds") Collection<Long> evenementIds);
}
