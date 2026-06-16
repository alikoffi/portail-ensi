package ci.inphb.ensi.portail.presentation.dto;

import java.util.List;

/**
 * Detail d'un appel : l'appel + la ligne de chaque membre (paye / reste).
 */
public class AppelDetailDto {

    private AppelCotisationDto appel;
    private List<LignePaiementDto> lignes;

    public AppelDetailDto(AppelCotisationDto appel, List<LignePaiementDto> lignes) {
        this.appel = appel;
        this.lignes = lignes;
    }

    public AppelCotisationDto getAppel() {
        return appel;
    }

    public List<LignePaiementDto> getLignes() {
        return lignes;
    }
}
