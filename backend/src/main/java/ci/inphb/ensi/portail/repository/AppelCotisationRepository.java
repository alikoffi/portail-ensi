package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.AppelCotisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppelCotisationRepository extends JpaRepository<AppelCotisation, Long> {

    List<AppelCotisation> findAllByOrderByDateButoirDesc();

    /** Appels dont l'echeance est passee (pour le calcul des retards). */
    List<AppelCotisation> findByDateButoirBefore(LocalDate date);
}
