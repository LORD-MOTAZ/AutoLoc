package tn.esprit.tpautoloc.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

@Entity
@Table(name = "agence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonAlias("id")
    private Long idAgence;
    private String nom;
    private String ville;
    private String adresse;
    private String telephone;

    // Étape 1 : Relation OneToMany avec Employe
    @OneToMany(mappedBy = "agence", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Employe> employes;

    // Étape 1 : Relation OneToMany avec Vehicule
    @OneToMany(mappedBy = "agence", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Vehicule> vehicules;

    public Long getId() {
        return idAgence;
    }
}
