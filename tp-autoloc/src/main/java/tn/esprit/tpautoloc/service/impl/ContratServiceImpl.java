package tn.esprit.tpautoloc.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpautoloc.domain.Contrat;
import tn.esprit.tpautoloc.domain.Reservation;
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
        return contratRepository.save(existant);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Contrat contrat = findById(id);            // 404 si absent
        Reservation reservation = contrat.getReservation();
        if (reservation != null) {
            reservation.setContrat(null);          // détache le lien côté Reservation
        }
        contratRepository.delete(contrat);         // supprime aussi les paiements (cascade)
    }

    private void validerMontant(Contrat contrat) {
        if (contrat.getMontantTotal() != null && contrat.getMontantTotal() < 0) {
            throw new IllegalArgumentException(
                    "Le montant total du contrat ne peut pas être négatif.");
        }
    }
}
