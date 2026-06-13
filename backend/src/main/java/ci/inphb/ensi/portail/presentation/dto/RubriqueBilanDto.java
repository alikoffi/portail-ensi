package ci.inphb.ensi.portail.presentation.dto;

import ci.inphb.ensi.portail.domain.RubriqueBilan;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RubriqueBilanDto {

    private Long id;

    @NotBlank(message = "La rubrique est obligatoire")
    private String rubrique;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal montant;

    public RubriqueBilanDto() {
    }

    public RubriqueBilanDto(RubriqueBilan rubriqueBilan) {
        this.id = rubriqueBilan.getId();
        this.rubrique = rubriqueBilan.getRubrique();
        this.montant = rubriqueBilan.getMontant();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRubrique() {
        return rubrique;
    }

    public void setRubrique(String rubrique) {
        this.rubrique = rubrique;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
}
