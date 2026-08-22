package ci.inphb.ensi.portail.enums;

/**
 * Avancement d'un evenement du planning. Le statut est saisi par le bureau ;
 * l'etat affiche (a venir / a cloturer / termine...) le combine avec la date.
 */
public enum StatutEvenement {

    /** Programme, pas encore cloture. */
    PLANIFIE,

    /** Evenement tenu et cloture par le bureau. */
    TERMINE,

    /** Evenement annule. */
    ANNULE,

    /** Reporte a une date ulterieure. */
    REPORTE
}
