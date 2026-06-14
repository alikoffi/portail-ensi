package ci.inphb.ensi.portail.domain;

import ci.inphb.ensi.portail.enums.RoleUtilisateur;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Compte utilisateur de la plateforme.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = Utilisateur.TABLE_NAME)
public class Utilisateur extends AbstractEntity {

    public static final String TABLE_NAME = "utilisateur";
    public static final String TABLE_ID = TABLE_NAME + ID;
    public static final String TABLE_SEQ = TABLE_ID + SEQ;

    @Id
    @SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleUtilisateur role;

    @Column(name = "label")
    private String label;

    @Column(name = "email")
    private String email;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    public Utilisateur() {
    }

    public Utilisateur(String username, String passwordHash, RoleUtilisateur role, String label) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.label = label;
        this.actif = true;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public RoleUtilisateur getRole() {
        return role;
    }

    public void setRole(RoleUtilisateur role) {
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
}
