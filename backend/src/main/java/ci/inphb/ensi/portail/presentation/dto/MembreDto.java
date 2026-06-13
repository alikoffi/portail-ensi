package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Membre;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class MembreDto {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String prenoms;
    private String matricule;

    @Email(message = "Adresse email invalide")
    private String email;

    private String telephone;
    private String specialite;

    @Pattern(regexp = "ACTIF|INACTIF", message = "Le statut doit être ACTIF ou INACTIF")
    private String statut;

    private LocalDate dateAdhesion;

    public MembreDto() {
    }

    public MembreDto(Membre m) {
        this.id = m.getId();
        this.nom = m.getNom();
        this.prenoms = m.getPrenoms();
        this.matricule = m.getMatricule();
        this.email = m.getEmail();
        this.telephone = m.getTelephone();
        this.specialite = m.getSpecialite();
        this.statut = m.getStatut().name();
        this.dateAdhesion = m.getDateAdhesion();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenoms() {
        return prenoms;
    }

    public void setPrenoms(String prenoms) {
        this.prenoms = prenoms;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDate getDateAdhesion() {
        return dateAdhesion;
    }

    public void setDateAdhesion(LocalDate dateAdhesion) {
        this.dateAdhesion = dateAdhesion;
    }
}
