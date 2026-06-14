package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.Membre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembreRepository extends JpaRepository<Membre, Long> {

    List<Membre> findAllByOrderByNomAscPrenomsAsc();
}
