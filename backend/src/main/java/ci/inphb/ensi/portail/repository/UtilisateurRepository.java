package ci.inphb.ensi.portail.repository;

import ci.inphb.ensi.portail.domain.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByUsername(String username);

    boolean existsByUsername(String username);

    /** Comptes actifs disposant d'une adresse email (destinataires des rappels). */
    java.util.List<Utilisateur> findByActifTrueAndEmailIsNotNull();
}
