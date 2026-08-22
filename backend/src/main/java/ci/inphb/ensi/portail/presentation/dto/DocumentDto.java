package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.Document;

import java.time.LocalDateTime;

/**
 * Piece jointe telechargeable. Le contenu binaire n'est jamais serialise :
 * il est recupere via {@code /ws/document/telecharger/{id}}.
 */
public class DocumentDto {

    private Long id;
    private Long pvId;
    private Long evenementId;
    private String libelle;
    private String nomOriginal;
    private String contentType;
    private long taille;
    private boolean principal;
    private String deposePar;
    private LocalDateTime deposeLe;

    public DocumentDto() {
    }

    public DocumentDto(Document document) {
        this.id = document.getId();
        this.pvId = document.getPv() != null ? document.getPv().getId() : null;
        this.evenementId = document.getEvenement() != null ? document.getEvenement().getId() : null;
        this.libelle = document.getLibelle();
        this.nomOriginal = document.getNomOriginal();
        this.contentType = document.getContentType();
        this.taille = document.getTaille();
        this.principal = document.isPrincipal();
        this.deposePar = document.getCreateBy();
        this.deposeLe = document.getCreateAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPvId() {
        return pvId;
    }

    public void setPvId(Long pvId) {
        this.pvId = pvId;
    }

    public Long getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(Long evenementId) {
        this.evenementId = evenementId;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getNomOriginal() {
        return nomOriginal;
    }

    public void setNomOriginal(String nomOriginal) {
        this.nomOriginal = nomOriginal;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getTaille() {
        return taille;
    }

    public void setTaille(long taille) {
        this.taille = taille;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public String getDeposePar() {
        return deposePar;
    }

    public void setDeposePar(String deposePar) {
        this.deposePar = deposePar;
    }

    public LocalDateTime getDeposeLe() {
        return deposeLe;
    }

    public void setDeposeLe(LocalDateTime deposeLe) {
        this.deposeLe = deposeLe;
    }
}
