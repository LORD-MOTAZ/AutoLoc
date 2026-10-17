package tn.esprit.tpautoloc.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tpautoloc.domain.Contrat;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;
import tn.esprit.tpautoloc.repository.IContratRepository;
import tn.esprit.tpautoloc.service.IContratService;

@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {
    private final IContratRepository contratRepository;
    @Override
    public Contrat create(Contrat contrat) {
        if (contrat.getIdContrat() != null) {
            throw new IllegalArgumentException(
                    "Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        validerMontant(contrat);
        return contratRepository.save(contrat);
    }
    @Override
    public Contrat findById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }
    @Override
    public List<Contrat> findAll() {



        return contratRepository.findAll();
    }
    @Override
    public Contrat update(Long id, Contrat contrat) {
        validerMontant(contrat);
        Contrat existant = findById(id);
        existant.setMontantTotal(contrat.getMontantTotal());
        existant.setDateSignature(contrat.getDateSignature());
        existant.setValide(contrat.getValide());
// recopier ici les autres attributs modifiables du contrat (Atelier 2),
// sans toucher à l'identifiant ni à la collection de paiements
        return contratRepository.save(existant);
    }
    @Override
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contrat", id);
        }
        contratRepository.deleteById(id);
    }

    // ---- règle de gestion ----
    private void validerMontant(Contrat contrat) {
        if (contrat.getMontantTotal() != null && contrat.getMontantTotal() < 0) {
            throw new IllegalArgumentException(
                    "Le montant total du contrat ne peut pas être négatif.");
        }
    }
}
