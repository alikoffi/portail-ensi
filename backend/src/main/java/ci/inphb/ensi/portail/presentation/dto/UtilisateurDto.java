package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Utilisateur;

public class UtilisateurDto {

    private Long id;
    private String username;
    private String role;
    private String label;
    private boolean actif;

    public UtilisateurDto() {
    }

    public UtilisateurDto(Utilisateur utilisateur) {
        this.id = utilisateur.getId();
        this.username = utilisateur.getUsername();
        this.role = utilisateur.getRole().name();
        this.label = utilisateur.getLabel();
        this.actif = utilisateur.isActif();
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

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
