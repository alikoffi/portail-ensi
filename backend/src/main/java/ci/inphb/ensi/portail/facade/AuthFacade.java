package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Utilisateur;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.auth.LoginRequest;
import ci.inphb.ensi.portail.presentation.dto.auth.LoginResponse;
import ci.inphb.ensi.portail.repository.UtilisateurRepository;
import ci.inphb.ensi.portail.security.JwtTokenUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestration de l'authentification.
 */
@Service
public class AuthFacade {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    public AuthFacade(UtilisateurRepository utilisateurRepository,
                      PasswordEncoder passwordEncoder,
                      JwtTokenUtils jwtTokenUtils) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByUsername(request.getUsername())
                .orElseThrow(PortailException::identifiantsInvalides);

        if (!utilisateur.isActif() || !passwordEncoder.matches(request.getPassword(), utilisateur.getPasswordHash())) {
            throw PortailException.identifiantsInvalides();
        }

        utilisateur.setDerniereConnexion(java.time.LocalDateTime.now());

        String token = jwtTokenUtils.generateToken(utilisateur);
        return new LoginResponse(token, utilisateur);
    }
}
