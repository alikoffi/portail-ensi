package ci.inphb.ensi.portail.enums;

import java.time.LocalDate;

/**
 * Etat affiche d'un evenement : combine le statut saisi par le bureau et la
 * date, afin de distinguer un evenement a venir d'un evenement passe que
 * personne n'a encore cloture.
 */
public enum EtatEvenement {

    /** Date future. */
    A_VENIR,

    /** Se tient aujourd'hui. */
    AUJOURDHUI,

    /** Date passee mais toujours au statut PLANIFIE : cloture oubliee. */
    A_CLOTURER,

    /** Cloture par le bureau. */
    TERMINE,

    /** Annule. */
    ANNULE,

    /** Reporte a une date ulterieure. */
    REPORTE;

    public static EtatEvenement calculer(StatutEvenement statut, LocalDate dateEvent) {
        if (statut == StatutEvenement.ANNULE) {
            return ANNULE;
        }
        if (statut == StatutEvenement.TERMINE) {
            return TERMINE;
        }
        if (statut == StatutEvenement.REPORTE) {
            return REPORTE;
        }
        if (dateEvent == null) {
            return A_VENIR;
        }
        LocalDate aujourdHui = LocalDate.now();
        if (dateEvent.isEqual(aujourdHui)) {
            return AUJOURDHUI;
        }
        return dateEvent.isAfter(aujourdHui) ? A_VENIR : A_CLOTURER;
    }
}
