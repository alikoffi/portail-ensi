package ci.inphb.ensi.portail.presentation.dto;

import jakarta.validation.constraints.Email;

/**
 * Mise a jour par l'utilisateur de ses propres informations.
 */
public class ProfilDto {

    private String label;

    @Email(message = "Adresse email invalide")
    private String email;

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
