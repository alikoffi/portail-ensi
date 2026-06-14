package ci.inphb.ensi.portail.enums;

/**
 * Roles applicatifs.
 * <ul>
 *   <li>ADMIN : acces complet</li>
 *   <li>TRESORIER : ecriture finances (transactions, bilan) et cotisations</li>
 *   <li>SECRETAIRE : ecriture proces-verbaux, planning et membres</li>
 *   <li>VIEWER : lecture seule</li>
 * </ul>
 */
public enum RoleUtilisateur {
    ADMIN,
    TRESORIER,
    SECRETAIRE,
    VIEWER
}
