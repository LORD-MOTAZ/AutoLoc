package tn.esprit.tpautoloc.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonAlias("id")
    private Long idContrat;
    private LocalDate dateSignature;
    private Double montantTotal;
    private Boolean valide;

    // Étape 5 : Relation inverse OneToOne avec Reservation
    @OneToOne
    private Reservation reservation;

    // Étape 5 : Relation OneToMany avec Paiement (Composition)
    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("contrat-paiements")
    private List<Paiement> paiements = new ArrayList<>();

    public Long getId() {
        return idContrat;
    }
}
