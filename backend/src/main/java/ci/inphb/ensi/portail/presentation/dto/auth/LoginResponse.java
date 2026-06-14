package ci.inphb.ensi.portail.presentation.dto.auth;

import ci.inphb.ensi.portail.domain.Utilisateur;

public class LoginResponse {

    private String token;
    private String username;
    private String role;
    private String label;

    public LoginResponse(String token, Utilisateur utilisateur) {
        this.token = token;
        this.username = utilisateur.getUsername();
        this.role = utilisateur.getRole().name();
        this.label = utilisateur.getLabel();
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getLabel() {
        return label;
    }
}
