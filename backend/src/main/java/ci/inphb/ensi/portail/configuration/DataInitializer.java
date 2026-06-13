package ci.inphb.ensi.portail.configuration;

import ci.inphb.ensi.portail.domain.Evenement;
import ci.inphb.ensi.portail.domain.Transaction;
import ci.inphb.ensi.portail.domain.Utilisateur;
import ci.inphb.ensi.portail.enums.RoleUtilisateur;
import ci.inphb.ensi.portail.enums.TypeTransaction;
import ci.inphb.ensi.portail.repository.EvenementRepository;
import ci.inphb.ensi.portail.repository.TransactionRepository;
import ci.inphb.ensi.portail.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Cree les comptes et des donnees de demonstration au premier demarrage (idempotent).
 * Mots de passe a changer imperativement en production.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final UtilisateurRepository utilisateurRepository;
    private final EvenementRepository evenementRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UtilisateurRepository utilisateurRepository,
                           EvenementRepository evenementRepository,
                           TransactionRepository transactionRepository,
                           PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.evenementRepository = evenementRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seederComptes();
        seederEvenements();
        seederTransactions();
    }

    private void seederComptes() {
        creerCompteSiAbsent("admin", "admin123", RoleUtilisateur.ADMIN, "Administrateur");
        creerCompteSiAbsent("visiteur1", "visiteur123", RoleUtilisateur.VIEWER, "Visiteur 1");
        creerCompteSiAbsent("visiteur2", "visiteur123", RoleUtilisateur.VIEWER, "Visiteur 2");
    }

    private void creerCompteSiAbsent(String username, String motDePasse, RoleUtilisateur role, String label) {
        if (utilisateurRepository.existsByUsername(username)) {
            return;
        }
        utilisateurRepository.save(new Utilisateur(username, passwordEncoder.encode(motDePasse), role, label));
        LOGGER.info("Compte par defaut cree : {} ({})", username, role);
    }

    private void seederEvenements() {
        if (evenementRepository.count() > 0) {
            return;
        }
        LocalDate aujourdHui = LocalDate.now();
        evenementRepository.save(creerEvenement("Assemblee generale de promotion", aujourdHui.plusDays(5),
                "18h00", "Reunion", "Amphi A - INPHB", "Bilan du semestre et perspectives."));
        evenementRepository.save(creerEvenement("Sortie de terrain - Topographie", aujourdHui.plusDays(12),
                "07h30", "Sortie", "Yamoussoukro", "Releve topographique encadre."));
        evenementRepository.save(creerEvenement("Soiree d'integration ENSI", aujourdHui.plusDays(20),
                "20h00", "Evenement", "Foyer des etudiants", "Accueil des nouveaux membres."));
        evenementRepository.save(creerEvenement("Reunion du bureau executif", aujourdHui.minusDays(3),
                "17h00", "Reunion", "Salle de reunion", "Preparation du budget."));
        LOGGER.info("Evenements de demonstration crees");
    }

    private Evenement creerEvenement(String nom, LocalDate date, String heure, String type, String lieu, String description) {
        Evenement e = new Evenement();
        e.setNom(nom);
        e.setDateEvent(date);
        e.setHeure(heure);
        e.setType(type);
        e.setLieu(lieu);
        e.setDescription(description);
        return e;
    }

    private void seederTransactions() {
        if (transactionRepository.count() > 0) {
            return;
        }
        LocalDate aujourdHui = LocalDate.now();
        transactionRepository.save(creerTransaction("Cotisations promotion - Janvier", TypeTransaction.RECETTE,
                250000, aujourdHui.minusDays(30), "Cotisations", "Versement de 50 membres"));
        transactionRepository.save(creerTransaction("Subvention direction", TypeTransaction.RECETTE,
                150000, aujourdHui.minusDays(18), "Subvention", null));
        transactionRepository.save(creerTransaction("Achat materiel evenement", TypeTransaction.DEPENSE,
                75000, aujourdHui.minusDays(10), "Logistique", "Sono et decoration"));
        transactionRepository.save(creerTransaction("Impression supports", TypeTransaction.DEPENSE,
                30000, aujourdHui.minusDays(4), "Communication", null));
        transactionRepository.save(creerTransaction("Cotisations promotion - Fevrier", TypeTransaction.RECETTE,
                120000, aujourdHui.minusDays(2), "Cotisations", null));
        LOGGER.info("Transactions de demonstration creees");
    }

    private Transaction creerTransaction(String libelle, TypeTransaction type, long montant,
                                         LocalDate date, String categorie, String note) {
        Transaction t = new Transaction();
        t.setLibelle(libelle);
        t.setType(type);
        t.setMontant(BigDecimal.valueOf(montant));
        t.setDateTx(date);
        t.setCategorie(categorie);
        t.setNote(note);
        return t;
    }
}
