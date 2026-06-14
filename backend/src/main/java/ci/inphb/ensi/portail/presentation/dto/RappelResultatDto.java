package ci.inphb.ensi.portail.presentation.dto;

/**
 * Resultat d'un declenchement de rappels.
 */
public class RappelResultatDto {

    private int nombreEvenements;
    private int nombreMembresEnRetard;
    private int nombreDestinataires;

    public RappelResultatDto(int nombreEvenements, int nombreMembresEnRetard, int nombreDestinataires) {
        this.nombreEvenements = nombreEvenements;
        this.nombreMembresEnRetard = nombreMembresEnRetard;
        this.nombreDestinataires = nombreDestinataires;
    }

    public int getNombreEvenements() {
        return nombreEvenements;
    }

    public int getNombreMembresEnRetard() {
        return nombreMembresEnRetard;
    }

    public int getNombreDestinataires() {
        return nombreDestinataires;
    }
}
