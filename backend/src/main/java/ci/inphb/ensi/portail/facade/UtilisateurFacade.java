package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Utilisateur;
import ci.inphb.ensi.portail.enums.RoleUtilisateur;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.ChangementMotDePasseDto;
import ci.inphb.ensi.portail.presentation.dto.CreationUtilisateurDto;
import ci.inphb.ensi.portail.presentation.dto.ModificationUtilisateurDto;
import ci.inphb.ensi.portail.presentation.dto.ProfilDto;
import ci.inphb.ensi.portail.presentation.dto.UtilisateurDto;
import ci.inphb.ensi.portail.repository.UtilisateurRepository;
import ci.inphb.ensi.portail.service.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UtilisateurFacade {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public UtilisateurFacade(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder,
                             MailService mailService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Transactional(readOnly = true)
    public List<UtilisateurDto> lister() {
        return utilisateurRepository.findAll().stream().map(UtilisateurDto::new).toList();
    }

    @Transactional(readOnly = true)
    public UtilisateurDto parUsername(String username) {
        return new UtilisateurDto(trouverParUsername(username));
    }

    @Transactional
    public UtilisateurDto enregistrer(CreationUtilisateurDto dto) {
        if (utilisateurRepository.existsByUsername(dto.getUsername().trim())) {
            throw PortailException.conflit("Ce nom d'utilisateur existe déjà");
        }
        Utilisateur utilisateur = new Utilisateur(
                dto.getUsername().trim(),
                passwordEncoder.encode(dto.getPassword()),
                RoleUtilisateur.valueOf(dto.getRole()),
                dto.getLabel()
        );
        utilisateur.setEmail(dto.getEmail());
        Utilisateur cree = utilisateurRepository.save(utilisateur);
        mailService.envoyerBienvenue(cree, dto.getPassword());
        return new UtilisateurDto(cree);
    }

    @Transactional
    public UtilisateurDto modifier(ModificationUtilisateurDto dto) {
        Utilisateur utilisateur = trouver(dto.getId());
        if (dto.getRole() != null) {
            utilisateur.setRole(RoleUtilisateur.valueOf(dto.getRole()));
        }
        utilisateur.setLabel(dto.getLabel());
        utilisateur.setEmail(dto.getEmail());
        return new UtilisateurDto(utilisateur);
    }

    @Transactional
    public UtilisateurDto basculerActif(Long id) {
        Utilisateur utilisateur = trouver(id);
        utilisateur.setActif(!utilisateur.isActif());
        return new UtilisateurDto(utilisateur);
    }

    // ---------- Profil de l'utilisateur connecte ----------
    @Transactional
    public UtilisateurDto modifierProfil(String username, ProfilDto dto) {
        Utilisateur utilisateur = trouverParUsername(username);
        utilisateur.setLabel(dto.getLabel());
        utilisateur.setEmail(dto.getEmail());
        return new UtilisateurDto(utilisateur);
    }

    @Transactional
    public void changerMotDePasse(String username, ChangementMotDePasseDto dto) {
        Utilisateur utilisateur = trouverParUsername(username);
        if (!passwordEncoder.matches(dto.getAncienMotDePasse(), utilisateur.getPasswordHash())) {
            throw new PortailException(HttpStatus.BAD_REQUEST, "L'ancien mot de passe est incorrect");
        }
        utilisateur.setPasswordHash(passwordEncoder.encode(dto.getNouveauMotDePasse()));
    }

    private Utilisateur trouver(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Utilisateur introuvable"));
    }

    private Utilisateur trouverParUsername(String username) {
        return utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> PortailException.nonTrouve("Utilisateur introuvable"));
    }
}
