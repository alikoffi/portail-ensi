package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.Ecran;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EcranRepository extends JpaRepository<Ecran, Long> {

    /** Tous les ecrans, dans l'ordre d'affichage du menu. */
    List<Ecran> findAllByOrderByOrdreAsc();

    /** Ecrans actuellement masques : sert au filtrage des appels d'API. */
    List<Ecran> findByVisibleFalse();

    Optional<Ecran> findByCode(String code);
}
