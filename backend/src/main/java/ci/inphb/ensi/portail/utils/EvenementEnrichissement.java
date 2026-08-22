package ci.inphb.ensi.portail.utils;

import ci.inphb.ensi.portail.domain.Pv;
import ci.inphb.ensi.portail.presentation.dto.EvenementDto;
import ci.inphb.ensi.portail.repository.DocumentRepository;
import ci.inphb.ensi.portail.repository.PvRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Complete des evenements avec leur proces-verbal et le nombre de pieces
 * jointes de celui-ci. Partage par le planning et le tableau de bord, en deux
 * requetes quel que soit le nombre d'evenements.
 */
public final class EvenementEnrichissement {

    private EvenementEnrichissement() {
    }

    public static void appliquer(List<EvenementDto> evenements,
                                 PvRepository pvRepository,
                                 DocumentRepository documentRepository) {
        List<Long> idsEvenement = evenements.stream()
                .map(EvenementDto::getId)
                .filter(Objects::nonNull)
                .toList();
        if (idsEvenement.isEmpty()) {
            return;
        }

        Map<Long, Pv> pvParEvenement = new HashMap<>();
        for (Pv pv : pvRepository.findByEvenementIdIn(idsEvenement)) {
            pvParEvenement.put(pv.getEvenement().getId(), pv);
        }
        if (pvParEvenement.isEmpty()) {
            return;
        }

        Map<Long, Long> documentsParPv = new HashMap<>();
        List<Long> idsPv = pvParEvenement.values().stream().map(Pv::getId).toList();
        for (Object[] ligne : documentRepository.compterParPv(idsPv)) {
            documentsParPv.put((Long) ligne[0], (Long) ligne[1]);
        }

        for (EvenementDto evenement : evenements) {
            Pv pv = pvParEvenement.get(evenement.getId());
            if (pv != null) {
                evenement.setPvId(pv.getId());
                evenement.setPvObjet(pv.getObjet());
                evenement.setNbDocuments(documentsParPv.getOrDefault(pv.getId(), 0L));
            }
        }
    }
}
