package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Utilisateur;
import ci.inphb.ensi.portail.enums.RoleUtilisateur;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.CreationUtilisateurDto;
import ci.inphb.ensi.portail.presentation.dto.UtilisateurDto;
import ci.inphb.ensi.portail.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UtilisateurFacade {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurFacade(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UtilisateurDto> lister() {
        return utilisateurRepository.findAll().stream().map(UtilisateurDto::new).toList();
    }

    @Transactional(readOnly = true)
    public UtilisateurDto parUsername(String username) {
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> PortailException.nonTrouve("Utilisateur introuvable"));
        return new UtilisateurDto(utilisateur);
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
        return new UtilisateurDto(utilisateurRepository.save(utilisateur));
    }

    @Transactional
    public UtilisateurDto basculerActif(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Utilisateur introuvable"));
        utilisateur.setActif(!utilisateur.isActif());
        return new UtilisateurDto(utilisateur);
    }
}
