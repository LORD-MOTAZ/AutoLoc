# service-notes.md — Atelier 4 : couche Service, IoC et injection de dépendances

---

## A. Modes d'injection comparés

| Critère | Injection par **constructeur** (`@RequiredArgsConstructor`) | Injection par **attribut** (`@Autowired`) |
|---|---|---|
| **Champ `final`** | ✅ Oui — le champ peut être déclaré `final`, garantissant l'immuabilité après construction | ❌ Non — Spring doit écrire le champ après la construction, donc il ne peut pas être `final` |
| **Dépendance visible** | ✅ Oui — chaque dépendance est un paramètre du constructeur, visible dans l'API publique de la classe | ❌ Non — la dépendance est cachée à l'intérieur du champ ; un appelant externe ne la voit pas sans réflexion |
| **Utilisation hors conteneur** (tests unitaires) | ✅ Facile — `new MonService(mockRepo)` suffit, aucun contexte Spring requis | ❌ Difficile — il faut un conteneur Spring ou de la réflexion (`ReflectionTestUtils`) pour injecter le mock |
| **Signalement SonarQube** | ✅ Recommandé — règle `java:S4449` encourage l'injection par constructeur | ⚠️ Signalé — SonarQube rapporte `java:S3305` / `java:S6813` : « Field injection should be avoided » |

> [!NOTE]
> L'annotation `@RequiredArgsConstructor` de Lombok génère automatiquement un constructeur avec
> tous les champs `final` non initialisés, ce qui évite d'écrire le constructeur manuellement
> tout en respectant les bonnes pratiques d'injection.

---

## B. Services — tableau récapitulatif

| Service | Classe d'implémentation | Dépendance injectée | Mode d'injection | Justification |
|---|---|---|---|---|
| `IAgenceService` | `AgenceServiceImpl` | `IAgenceRepository` | Constructeur (`@RequiredArgsConstructor`) | Champ `final`, testable hors conteneur, conforme SonarQube |
| `IEmployeService` | `EmployeServiceImpl` | `IEmployeRepository` | Constructeur (`@RequiredArgsConstructor`) | Champ `final`, testable hors conteneur, conforme SonarQube |
| `IVehiculeService` | `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur (`@RequiredArgsConstructor`) | Champ `final`, testable hors conteneur, conforme SonarQube |
| `IEquipementService` | `EquipementServiceImpl` | `IEquipementRepository` | Constructeur (`@RequiredArgsConstructor`) | Champ `final`, testable hors conteneur, conforme SonarQube |
| `IClientService` | `ClientServiceImpl` | `IClientRepository` | Constructeur (`@RequiredArgsConstructor`) | Champ `final`, testable hors conteneur, conforme SonarQube |
| `IReservationService` | `ReservationServiceImpl` | `IReservationRepository` | Constructeur (`@RequiredArgsConstructor`) | Champ `final`, testable hors conteneur, conforme SonarQube |
| `IContratService` | `ContratServiceImpl` | `IContratRepository` | Constructeur (`@RequiredArgsConstructor`) | Champ `final`, testable hors conteneur, conforme SonarQube |
| `IPaiementService` | `PaiementServiceImpl` | `IPaiementRepository` | Constructeur (`@RequiredArgsConstructor`) | Champ `final`, testable hors conteneur, conforme SonarQube |
| `IMaintenanceService` | `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur (`@RequiredArgsConstructor`) | Champ `final`, testable hors conteneur, conforme SonarQube |

> [!TIP]
> Tous les services utilisent l'injection par constructeur via `@RequiredArgsConstructor`.
> Cela garantit qu'aucun service ne peut être instancié avec des dépendances nulles.

---

## C. Messages d'erreur — causes et corrections

### Message 1 : `IllegalArgumentException: Le nom de l'agence est obligatoire et ne peut pas être vide.`

| | Détail |
|---|---|
| **Cause** | Le client de l'API (ou un test) a appelé `create` ou `update` en passant un objet `Agence` dont le champ `nom` est `null` ou ne contient que des espaces. La règle de gestion dans `AgenceServiceImpl.validerAttributsObligatoires()` lève cette exception avant toute persistance. |
| **Correction** | S'assurer que le champ `nom` est renseigné avec une valeur non vide dans l'objet `Agence` passé à l'appel. Exemple : `agence.setNom("Agence Tunis Centre");` avant d'appeler `agenceService.create(agence)`. À partir du Chapitre 10, l'annotation `@NotBlank` sur le champ remplacera cette vérification manuelle. |

---

### Message 2 : `IllegalArgumentException: Le tarif journalier du véhicule ne peut pas être négatif.`

| | Détail |
|---|---|
| **Cause** | Le client de l'API a fourni un `tarifJournalier` négatif (ex. `-50.00`). La règle de gestion dans `VehiculeServiceImpl.validerValeursNumeriques()` rejette toute valeur inférieure à zéro. |
| **Correction** | Fournir une valeur de tarif journalier supérieure ou égale à zéro. Exemple : `vehicule.setTarifJournalier(new BigDecimal("85.00"));`. Si le tarif n'est pas encore connu, laisser le champ `null` (la validation ne s'applique que si la valeur est présente). |

---

### Message 3 : `IllegalArgumentException: La date de fin de la réservation doit être postérieure à la date de début.`

| | Détail |
|---|---|
| **Cause** | Le client de l'API a soumis une réservation dont `dateFin` est égale ou antérieure à `dateDebut` (ex. réservation du 20 au 18 octobre). La règle de gestion dans `ReservationServiceImpl.validerDates()` effectue le contrôle `dateFin.isAfter(dateDebut)`. |
| **Correction** | Vérifier que `dateFin` est strictement postérieure à `dateDebut`. Exemple : `reservation.setDateDebut(LocalDate.of(2026, 10, 20)); reservation.setDateFin(LocalDate.of(2026, 10, 25));`. |
