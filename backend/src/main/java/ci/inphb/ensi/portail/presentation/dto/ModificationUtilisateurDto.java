package ci.inphb.ensi.portail.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Modification d'un compte par un administrateur (role, libelle, email).
 */
public class ModificationUtilisateurDto {

    @NotNull(message = "L'identifiant est obligatoire")
    private Long id;

    @Pattern(regexp = "ADMIN|TRESORIER|SECRETAIRE|VIEWER", message = "Rôle invalide")
    private String role;

    private String label;

    @Email(message = "Adresse email invalide")
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
