package ci.inphb.ensi.portail.facade;

import ci.inphb.ensi.portail.domain.Parametrage;
import ci.inphb.ensi.portail.enums.CategorieParametrage;
import ci.inphb.ensi.portail.exception.PortailException;
import ci.inphb.ensi.portail.presentation.dto.ParametrageDto;
import ci.inphb.ensi.portail.repository.ParametrageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gestion des valeurs de reference (parametrage).
 */
@Service
public class ParametrageFacade {

    private final ParametrageRepository parametrageRepository;

    public ParametrageFacade(ParametrageRepository parametrageRepository) {
        this.parametrageRepository = parametrageRepository;
    }

    @Transactional(readOnly = true)
    public List<ParametrageDto> lister(String categorie) {
        return parametrageRepository.findByCategorieOrderByOrdreAscLibelleAsc(valider(categorie))
                .stream().map(ParametrageDto::new).toList();
    }

    @Transactional(readOnly = true)
    public List<ParametrageDto> listerActifs(String categorie) {
        return parametrageRepository.findByCategorieAndActifTrueOrderByOrdreAscLibelleAsc(valider(categorie))
                .stream().map(ParametrageDto::new).toList();
    }

    @Transactional
    public ParametrageDto enregistrer(ParametrageDto dto) {
        String categorie = valider(dto.getCategorie());
        if (dto.getId() == null) {
            if (parametrageRepository.existsByCategorieAndLibelleIgnoreCase(categorie, dto.getLibelle().trim())) {
                throw PortailException.conflit("Cette valeur existe déjà");
            }
            Parametrage parametrage = new Parametrage();
            parametrage.mettreAJour(categorie, dto.getLibelle().trim(), dto.getOrdre(), dto.isActif());
            return new ParametrageDto(parametrageRepository.save(parametrage));
        }
        Parametrage parametrage = parametrageRepository.findById(dto.getId())
                .orElseThrow(() -> PortailException.nonTrouve("Valeur introuvable"));
        parametrage.mettreAJour(categorie, dto.getLibelle().trim(), dto.getOrdre(), dto.isActif());
        return new ParametrageDto(parametrage);
    }

    @Transactional
    public void supprimer(Long id) {
        Parametrage parametrage = parametrageRepository.findById(id)
                .orElseThrow(() -> PortailException.nonTrouve("Valeur introuvable"));
        parametrageRepository.delete(parametrage);
    }

    private String valider(String categorie) {
        try {
            return CategorieParametrage.valueOf(categorie).name();
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw PortailException.nonTrouve("Catégorie de paramétrage inconnue : " + categorie);
        }
    }
}
