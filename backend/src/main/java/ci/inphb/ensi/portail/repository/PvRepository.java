package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.Pv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PvRepository extends JpaRepository<Pv, Long> {

    /** Tous les proces-verbaux, du plus recent au plus ancien. */
    List<Pv> findAllByOrderByDatePvDesc();
}
