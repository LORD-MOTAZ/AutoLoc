package tn.esprit.tpautoloc.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.tpautoloc.domain.enums.ModePaiement;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonAlias("id")
    private Long idPaiement;
    private Double montant;
    private LocalDate datePaiement;
    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    // Étape 6 : Relation inverse ManyToOne avec Contrat (Composition)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference("contrat-paiements")
    private Contrat contrat;

    public Long getId() {
        return idPaiement;
    }
}
