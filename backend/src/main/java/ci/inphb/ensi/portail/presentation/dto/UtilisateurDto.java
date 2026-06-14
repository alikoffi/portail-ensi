package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Utilisateur;

import java.time.LocalDateTime;

public class UtilisateurDto {

    private Long id;
    private String username;
    private String role;
    private String label;
    private String email;
    private boolean actif;
    private LocalDateTime derniereConnexion;

    public UtilisateurDto() {
    }

    public UtilisateurDto(Utilisateur utilisateur) {
        this.id = utilisateur.getId();
        this.username = utilisateur.getUsername();
        this.role = utilisateur.getRole().name();
        this.label = utilisateur.getLabel();
        this.email = utilisateur.getEmail();
        this.actif = utilisateur.isActif();
        this.derniereConnexion = utilisateur.getDerniereConnexion();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(LocalDateTime derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }
}
