package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Utilisateur;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.UtilisateurDto;
import ci.inphb.ensi.portail.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UtilisateurFacade {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurFacade(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
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
}
