# api-notes.md — Atelier 5 : couche Web, SpringDoc et documentation API

---

## A. Configuration générale

L'application tourne sur le port **8081** avec le préfixe de contexte `/api`.
Toutes les URL des endpoints commencent donc par `http://localhost:8081/api`.

Le fichier `application.properties` contient les deux lignes responsables de cette configuration :

```properties
server.port=8081
server.servlet.context-path=/api
```

La base de données utilisée est MySQL (`autoloc_db`), créée automatiquement au premier démarrage grâce au paramètre `createDatabaseIfNotExist=true` dans l'URL de connexion. Hibernate gère le schéma via `ddl-auto=update`.

---

## B. Endpoints exposés

L'API expose 9 groupes de ressources. Chaque groupe correspond à une entité du domaine métier.

| Ressource | URL de base | Opérations |
|---|---|---|
| Agences | `/api/agences` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Employes | `/api/employes` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Vehicules | `/api/vehicules` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Equipements | `/api/equipements` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Clients | `/api/clients` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Reservations | `/api/reservations` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Contrats | `/api/contrats` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Paiements | `/api/paiements` | GET, GET/{id} uniquement (lecture seule) |
| Maintenances | `/api/maintenances` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |

Le groupe **Paiements** est intentionnellement limité à la lecture. Les paiements sont créés exclusivement via le contrat parent grâce à la cascade JPA (`CascadeType.ALL`). Appeler `POST /api/paiements` retourne une erreur `405 Method Not Allowed`.

---

## C. Gestion des erreurs

Un composant `GlobalExceptionHandler` annoté `@RestControllerAdvice` intercepte les exceptions levées par la couche service et les transforme en réponses JSON structurées, sans exposer la stack trace à l'appelant.

**Format de la réponse d'erreur :**

```json
{
  "statut": 404,
  "message": "Contrat introuvable avec l'identifiant 9999",
  "horodatage": "2026-10-27T11:45:00"
}
```

**Codes HTTP utilisés :**

| Code | Situation |
|---|---|
| 200 OK | Lecture ou modification réussie |
| 201 Created | Création réussie |
| 204 No Content | Suppression réussie |
| 400 Bad Request | Règle de gestion violée (valeur invalide, champ manquant, dates incohérentes) |
| 404 Not Found | Ressource absente en base de données |
| 405 Method Not Allowed | Méthode HTTP non déclarée dans le contrôleur (ex. POST sur /paiements) |

---

## D. Anomalies SonarQube et décisions prises

L'analyse SonarQube de la couche web a remonté 3 types d'anomalies.

### Anomalie 1 — `@ApiResponses` wrapper inutile (corrigée)

**Règle :** `java:S1854` / OpenAPI best practice  
**Fichiers concernés :** `ContratController.java`, `PaiementController.java`

Lors de la première écriture des contrôleurs, les annotations `@ApiResponse` avaient été regroupées dans un wrapper `@ApiResponses({ ... })`. En OpenAPI v3 pour Java, `@ApiResponse` est déclarée comme `@Repeatable`, ce qui rend le wrapper conteneur inutile. SonarQube le signale comme redondant.

**Correction appliquée :** Les annotations `@ApiResponse` ont été placées directement les unes sous les autres sur chaque méthode, sans enveloppe.

---

### Anomalie 2 — Fuseau horaire implicite dans `LocalDateTime.now()` (corrigée)

**Règle :** `java:S6544`  
**Fichier concerné :** `GlobalExceptionHandler.java`

Appeler `LocalDateTime.now()` sans argument s'appuie sur le fuseau horaire de la JVM, qui peut différer selon l'environnement d'exécution (serveur en UTC, machine de développement en GMT+1). Le comportement devient alors imprévisible lors d'un déploiement cloud.

**Correction appliquée :** Le fuseau est désormais passé explicitement :

```java
LocalDateTime.now(ZoneId.systemDefault())
```

---

### Anomalie 3 — Entités JPA exposées directement dans les contrôleurs (conservée temporairement)

**Règle :** `java:S4684`  
**Fichiers concernés :** tous les contrôleurs (`ContratController`, `AgenceController`, etc.)

Passer directement une classe annotée `@Entity` comme paramètre `@RequestBody` ou comme type de retour dans un contrôleur crée un couplage fort entre l'API REST et le schéma de base de données. Un champ ajouté en base devient immédiatement visible dans l'API, et un champ mal désérialisé peut corrompre des données persistées.

**Décision :** Cette anomalie est conservée intentionnellement pour cet atelier. Le sujet précise que les entités JPA sont exposées directement à titre provisoire. Le Chapitre 10 introduira des objets de transfert (DTO) qui découpleront proprement la représentation API de la structure de persistance.

---

## E. Documentation Swagger UI

SpringDoc OpenAPI (`springdoc-openapi-starter-webmvc-ui` version `3.0.0`, compatible Spring Boot 4.x) est intégré au projet.

La documentation interactive est accessible à l'adresse :

```
http://localhost:8081/api/swagger-ui.html
```

La documentation brute au format JSON OpenAPI 3 est disponible à :

```
http://localhost:8081/api/v3/api-docs
```

Les métadonnées de l'API (titre, description, version) sont définies dans la classe `OpenApiConfig` via un bean `OpenAPI` :

```java
@Bean
public OpenAPI autolocOpenApi() {
    return new OpenAPI().info(new Info()
            .title("AutoLoc API")
            .description("API REST de gestion de location de véhicules")
            .version("1.0.0"));
}
```

Chaque contrôleur est documenté avec les annotations suivantes :

- `@Tag` sur la classe — regroupe les endpoints sous un nom de section lisible dans Swagger UI.
- `@Operation` sur chaque méthode — donne un résumé descriptif de l'action.
- `@ApiResponse` sur chaque méthode — liste les codes HTTP possibles avec leur signification.
- `@Parameter` sur les paramètres de chemin (`@PathVariable`) — décrit le rôle de l'identifiant.

---

## F. Cascade Contrat vers Paiement

La relation entre `Contrat` et `Paiement` est une composition : un paiement n'a pas de sens en dehors de son contrat.

En JPA, cela se traduit par `@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)` côté `Contrat`, et `@ManyToOne` côté `Paiement`. Lorsqu'un contrat est créé avec une liste de paiements dans le corps de la requête, Hibernate insère automatiquement les paiements en base sans qu'aucun appel explicite à `paiementRepository.save()` soit nécessaire. C'est la cascade `PERSIST` qui s'en charge.

Pour éviter les boucles infinies lors de la sérialisation JSON, le couple `@JsonManagedReference` (sur `Contrat.paiements`) et `@JsonBackReference` (sur `Paiement.contrat`) a été utilisé. Cela permet à Jackson de sérialiser la liste des paiements dans la réponse du contrat, tout en empêchant la boucle retour depuis le paiement vers le contrat.

**Exemple de corps POST pour créer un contrat avec ses paiements :**

```json
{
  "dateSignature": "2026-11-02",
  "montantTotal": 480.00,
  "valide": true,
  "paiements": [
    { "montant": 150.00, "datePaiement": "2026-11-02", "modePaiement": "CARTE" },
    { "montant": 330.00, "datePaiement": "2026-11-06", "modePaiement": "VIREMENT" }
  ]
}
```

Les deux paiements apparaissent ensuite dans `GET /api/paiements` sans avoir jamais été soumis directement à ce endpoint.
