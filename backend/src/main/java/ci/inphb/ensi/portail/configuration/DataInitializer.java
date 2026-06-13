package ci.inphb.ensi.portail.configuration;

import ci.inphb.ensi.portail.domain.Utilisateur;
import ci.inphb.ensi.portail.enums.RoleUtilisateur;
import ci.inphb.ensi.portail.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cree les comptes par defaut au premier demarrage (idempotent).
 * Mots de passe a changer imperativement en production.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        creerSiAbsent("admin", "admin123", RoleUtilisateur.ADMIN, "Administrateur");
        creerSiAbsent("visiteur1", "visiteur123", RoleUtilisateur.VIEWER, "Visiteur 1");
        creerSiAbsent("visiteur2", "visiteur123", RoleUtilisateur.VIEWER, "Visiteur 2");
    }

    private void creerSiAbsent(String username, String motDePasse, RoleUtilisateur role, String label) {
        if (utilisateurRepository.existsByUsername(username)) {
            return;
        }
        Utilisateur utilisateur = new Utilisateur(username, passwordEncoder.encode(motDePasse), role, label);
        utilisateurRepository.save(utilisateur);
        LOGGER.info("Compte par defaut cree : {} ({})", username, role);
    }
}
