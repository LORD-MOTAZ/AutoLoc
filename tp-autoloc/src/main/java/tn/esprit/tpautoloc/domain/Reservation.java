package tn.esprit.tpautoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.tpautoloc.domain.enums.StatutReservation;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    // Étape 4 : Relation ManyToOne avec Client
    @ManyToOne
    private Client client;

    // Étape 4 : Relation ManyToOne avec Vehicule
    @ManyToOne
    private Vehicule vehicule;

    // Étape 4 : Relation OneToOne avec Contrat
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    @JsonIgnore
    private Contrat contrat;
}
