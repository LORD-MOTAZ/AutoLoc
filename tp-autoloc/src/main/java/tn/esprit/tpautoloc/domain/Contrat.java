package tn.esprit.tpautoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContrat;
    private LocalDate dateSignature;
    private Double montantTotal;
    private Boolean valide;

    // Étape 5 : Relation inverse OneToOne avec Reservation
    @OneToOne
    private Reservation reservation;

    // Étape 5 : Relation OneToMany avec Paiement
    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Paiement> paiements;
}
