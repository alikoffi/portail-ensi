package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.BilanPassif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface BilanPassifRepository extends JpaRepository<BilanPassif, Long> {

    List<BilanPassif> findAllByOrderByIdAsc();

    @Query("SELECT COALESCE(SUM(b.montant), 0) FROM BilanPassif b")
    BigDecimal total();
}
