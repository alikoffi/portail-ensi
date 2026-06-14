package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.Parametrage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParametrageRepository extends JpaRepository<Parametrage, Long> {

    /** Toutes les valeurs d'une categorie (admin), triees. */
    List<Parametrage> findByCategorieOrderByOrdreAscLibelleAsc(String categorie);

    /** Valeurs actives d'une categorie (pour les listes deroulantes). */
    List<Parametrage> findByCategorieAndActifTrueOrderByOrdreAscLibelleAsc(String categorie);

    boolean existsByCategorieAndLibelleIgnoreCase(String categorie, String libelle);
}
