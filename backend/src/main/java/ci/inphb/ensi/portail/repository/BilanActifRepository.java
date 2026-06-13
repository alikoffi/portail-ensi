package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.BilanActif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface BilanActifRepository extends JpaRepository<BilanActif, Long> {

    List<BilanActif> findAllByOrderByIdAsc();

    @Query("SELECT COALESCE(SUM(b.montant), 0) FROM BilanActif b")
    BigDecimal total();
}
