package tn.esprit.tpautoloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tn.esprit.tpautoloc.domain.*;
import tn.esprit.tpautoloc.domain.enums.*;
import tn.esprit.tpautoloc.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TpAutolocApplicationTests {

    // =========================================================
    // Partie 2 — IAgenceService
    // =========================================================
    @Autowired
    private IAgenceService agenceService;

    // =========================================================
    // Partie 3 — IClientService
    // =========================================================
    @Autowired
    private IClientService clientService;

    // =========================================================
    // Partie 4 — IVehiculeService
    // =========================================================
    @Autowired
    private IVehiculeService vehiculeService;

    // =========================================================
    // Partie 5 — Exercice autonome (3 services au choix)
    // =========================================================
    @Autowired
    private IEmployeService employeService;

    @Autowired
    private IMaintenanceService maintenanceService;

    @Autowired
    private IEquipementService equipementService;

    // ---------------------------------------------------------
    @Test
    void contextLoads() {
        // Vérifie que le contexte Spring démarre sans erreur
    }

    // =========================================================
    // Partie 2 — testAgenceService
    // =========================================================
    @Test
    void testAgenceService() {
        System.out.println("\n========== TEST AGENCE SERVICE ==========");

        // --- Création d'une agence ---
        Agence agence = new Agence();
        agence.setNom("Agence Tunis Centre");
        agence.setVille("Tunis");
        agence.setAdresse("12 Avenue Habib Bourguiba");
        agence.setTelephone("71123456");

        // --- Enregistrement ---
        Agence agenceSauvegardee = agenceService.create(agence);
        System.out.println("Agence enregistrée : " + agenceSauvegardee);
        System.out.println("  ID  : " + agenceSauvegardee.getIdAgence());
        System.out.println("  Nom : " + agenceSauvegardee.getNom());
        assertNotNull(agenceSauvegardee.getIdAgence(),
                "L'ID généré ne doit pas être null après persistance.");

        // --- findAll() ---
        List<Agence> toutesLesAgences = agenceService.findAll();
        System.out.println("\nListe de toutes les agences (" + toutesLesAgences.size() + ") :");
        toutesLesAgences.forEach(a -> System.out.println("  - [" + a.getIdAgence() + "] " + a.getNom()));
        assertFalse(toutesLesAgences.isEmpty(), "La liste des agences ne doit pas être vide.");

        // --- findById() ---
        Agence agenceTrouvee = agenceService.findById(agenceSauvegardee.getIdAgence());
        System.out.println("\nAgence trouvée par ID " + agenceSauvegardee.getIdAgence()
                + " : " + agenceTrouvee.getNom());
        assertNotNull(agenceTrouvee, "L'agence recherchée par ID ne doit pas être null.");
        assertEquals(agenceSauvegardee.getIdAgence(), agenceTrouvee.getIdAgence());

        // --- count() (via findAll().size()) ---
        long nombreAgences = agenceService.findAll().size();
        System.out.println("\nNombre d'agences en base : " + nombreAgences);
        assertTrue(nombreAgences > 0, "Il doit y avoir au moins une agence en base.");

        // --- deleteById() + vérification ---
        agenceService.deleteById(agenceSauvegardee.getIdAgence());
        System.out.println("\nAgence supprimée (ID " + agenceSauvegardee.getIdAgence() + ").");

        // Vérification : findById doit lever ResourceNotFoundException
        assertThrows(
                Exception.class,
                () -> agenceService.findById(agenceSauvegardee.getIdAgence()),
                "findById après suppression doit lever une exception."
        );
        System.out.println("Vérification OK : l'agence n'existe plus en base.");
        System.out.println("=========================================\n");
    }

    // =========================================================
    // Partie 3 — testClientService
    // =========================================================
    @Test
    void testClientService() {
        System.out.println("\n========== TEST CLIENT SERVICE ==========");

        // --- Création de deux clients ---
        Client client1 = new Client();
        client1.setNom("Ben Salah");
        client1.setPrenom("Amira");
        client1.setEmail("amira.bensalah@email.com");
        client1.setTelephone("98111222");
        client1.setNumPermis("TN123456");
        client1.setDateInscription(LocalDate.now());

        Client client2 = new Client();
        client2.setNom("Trabelsi");
        client2.setPrenom("Yassine");
        client2.setEmail("yassine.trabelsi@email.com");
        client2.setTelephone("98333444");
        client2.setNumPermis("TN789012");
        client2.setDateInscription(LocalDate.now());

        // --- Enregistrement des deux clients ---
        Client c1Sauvegarde = clientService.create(client1);
        Client c2Sauvegarde = clientService.create(client2);
        System.out.println("Client 1 enregistré : [" + c1Sauvegarde.getIdClient()
                + "] " + c1Sauvegarde.getNom() + " " + c1Sauvegarde.getPrenom());
        System.out.println("Client 2 enregistré : [" + c2Sauvegarde.getIdClient()
                + "] " + c2Sauvegarde.getNom() + " " + c2Sauvegarde.getPrenom());
        assertNotNull(c1Sauvegarde.getIdClient(), "L'ID du client 1 ne doit pas être null.");
        assertNotNull(c2Sauvegarde.getIdClient(), "L'ID du client 2 ne doit pas être null.");
        assertNotEquals(c1Sauvegarde.getIdClient(), c2Sauvegarde.getIdClient(),
                "Les deux clients doivent avoir des IDs différents.");

        // --- findAll() ---
        List<Client> tousLesClients = clientService.findAll();
        System.out.println("\nListe de tous les clients (" + tousLesClients.size() + ") :");
        tousLesClients.forEach(c -> System.out.println(
                "  - [" + c.getIdClient() + "] " + c.getNom() + " " + c.getPrenom()
                        + " | email: " + c.getEmail()));
        assertTrue(tousLesClients.size() >= 2,
                "Il doit y avoir au moins 2 clients après l'enregistrement.");

        // --- findById() ---
        Client clientTrouve = clientService.findById(c1Sauvegarde.getIdClient());
        System.out.println("\nClient trouvé par ID " + c1Sauvegarde.getIdClient()
                + " : " + clientTrouve.getNom() + " " + clientTrouve.getPrenom());
        assertNotNull(clientTrouve, "Le client recherché par ID ne doit pas être null.");
        assertEquals("Ben Salah", clientTrouve.getNom());

        // --- count() (via findAll().size()) ---
        long nombreClients = clientService.findAll().size();
        System.out.println("\nNombre de clients en base : " + nombreClients);
        assertTrue(nombreClients >= 2);

        // --- deleteById() client 1 + vérification ---
        clientService.deleteById(c1Sauvegarde.getIdClient());
        System.out.println("\nClient 1 supprimé (ID " + c1Sauvegarde.getIdClient() + ").");
        assertThrows(
                Exception.class,
                () -> clientService.findById(c1Sauvegarde.getIdClient()),
                "findById après suppression doit lever une exception."
        );
        System.out.println("Vérification OK : le client 1 n'existe plus en base.");

        // Nettoyage client 2
        clientService.deleteById(c2Sauvegarde.getIdClient());
        System.out.println("Client 2 supprimé (nettoyage).");
        System.out.println("=========================================\n");
    }

    // =========================================================
    // Partie 4 — testVehiculeService
    // =========================================================
    @Test
    void testVehiculeService() {
        System.out.println("\n========== TEST VEHICULE SERVICE ==========");

        // --- Création d'un véhicule ---
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("123 TUN 4567");
        vehicule.setMarque("Renault");
        vehicule.setModele("Clio");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("80.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);

        // --- Enregistrement ---
        Vehicule vehiculeSauvegarde = vehiculeService.create(vehicule);
        System.out.println("Véhicule enregistré : [" + vehiculeSauvegarde.getIdVehicule()
                + "] " + vehiculeSauvegarde.getMarque() + " " + vehiculeSauvegarde.getModele()
                + " | tarif : " + vehiculeSauvegarde.getTarifJournalier() + " TND/j");
        assertNotNull(vehiculeSauvegarde.getIdVehicule(),
                "L'ID du véhicule ne doit pas être null.");
        assertEquals(new BigDecimal("80.00"), vehiculeSauvegarde.getTarifJournalier());

        // --- findAll() ---
        List<Vehicule> tousLesVehicules = vehiculeService.findAll();
        System.out.println("\nListe de tous les véhicules (" + tousLesVehicules.size() + ") :");
        tousLesVehicules.forEach(v -> System.out.println(
                "  - [" + v.getIdVehicule() + "] " + v.getMarque()
                        + " " + v.getModele() + " | statut: " + v.getStatut()));
        assertFalse(tousLesVehicules.isEmpty());

        // --- findById() ---
        Vehicule vehiculeTrouve = vehiculeService.findById(vehiculeSauvegarde.getIdVehicule());
        System.out.println("\nVéhicule trouvé par ID " + vehiculeSauvegarde.getIdVehicule()
                + " : " + vehiculeTrouve.getMarque() + " " + vehiculeTrouve.getModele());
        assertNotNull(vehiculeTrouve);
        assertEquals("Renault", vehiculeTrouve.getMarque());

        // --- count() (via findAll().size()) ---
        long nombreVehicules = vehiculeService.findAll().size();
        System.out.println("\nNombre de véhicules en base : " + nombreVehicules);
        assertTrue(nombreVehicules > 0);

        // --- deleteById() + vérification ---
        vehiculeService.deleteById(vehiculeSauvegarde.getIdVehicule());
        System.out.println("\nVéhicule supprimé (ID " + vehiculeSauvegarde.getIdVehicule() + ").");
        assertThrows(
                Exception.class,
                () -> vehiculeService.findById(vehiculeSauvegarde.getIdVehicule()),
                "findById après suppression doit lever une exception."
        );
        System.out.println("Vérification OK : le véhicule n'existe plus en base.");
        System.out.println("==========================================\n");
    }

    // =========================================================
    // Partie 5 — Exercice autonome : EmployeService
    // =========================================================
    @Test
    void testEmployeService() {
        System.out.println("\n========== TEST EMPLOYE SERVICE ==========");

        // --- Création ---
        Employe employe = new Employe();
        employe.setNom("Khemiri");
        employe.setPrenom("Sami");
        employe.setRole(RoleEmploye.AGENT);

        // save()
        Employe employeSauvegarde = employeService.create(employe);
        System.out.println("Employé enregistré : [" + employeSauvegarde.getIdEmploye()
                + "] " + employeSauvegarde.getNom() + " " + employeSauvegarde.getPrenom()
                + " | rôle : " + employeSauvegarde.getRole());
        assertNotNull(employeSauvegarde.getIdEmploye());

        // findAll()
        List<Employe> tousLesEmployes = employeService.findAll();
        System.out.println("\nListe des employés (" + tousLesEmployes.size() + ") :");
        tousLesEmployes.forEach(e -> System.out.println(
                "  - [" + e.getIdEmploye() + "] " + e.getNom() + " | " + e.getRole()));
        assertFalse(tousLesEmployes.isEmpty());

        // findById()
        Employe employeTrouve = employeService.findById(employeSauvegarde.getIdEmploye());
        assertNotNull(employeTrouve);
        assertEquals("Khemiri", employeTrouve.getNom());
        System.out.println("\nEmployé trouvé par ID : " + employeTrouve.getNom());

        // existsById() — simulé via findById sans exception
        boolean existe = true;
        try {
            employeService.findById(employeSauvegarde.getIdEmploye());
        } catch (Exception ex) {
            existe = false;
        }
        assertTrue(existe, "L'employé doit exister avant suppression.");
        System.out.println("L'employé existe avant suppression : " + existe);

        // deleteById()
        employeService.deleteById(employeSauvegarde.getIdEmploye());
        System.out.println("\nEmployé supprimé (ID " + employeSauvegarde.getIdEmploye() + ").");
        assertThrows(Exception.class,
                () -> employeService.findById(employeSauvegarde.getIdEmploye()));
        System.out.println("Vérification OK : l'employé n'existe plus.");
        System.out.println("==========================================\n");
    }

    // =========================================================
    // Partie 5 — Exercice autonome : MaintenanceService
    // =========================================================
    @Test
    void testMaintenanceService() {
        System.out.println("\n========== TEST MAINTENANCE SERVICE ==========");

        // --- Création ---
        Maintenance maintenance = new Maintenance();
        maintenance.setDateDebut(LocalDate.now());
        maintenance.setDateFin(LocalDate.now().plusDays(3));
        maintenance.setDescription("Vidange et contrôle freins");

        // save()
        Maintenance maintenanceSauvegardee = maintenanceService.create(maintenance);
        System.out.println("Maintenance enregistrée : [" + maintenanceSauvegardee.getIdMaintenance()
                + "] " + maintenanceSauvegardee.getDescription()
                + " | du " + maintenanceSauvegardee.getDateDebut()
                + " au " + maintenanceSauvegardee.getDateFin());
        assertNotNull(maintenanceSauvegardee.getIdMaintenance());

        // findAll()
        List<Maintenance> toutesLesMaintenances = maintenanceService.findAll();
        System.out.println("\nListe des maintenances (" + toutesLesMaintenances.size() + ") :");
        toutesLesMaintenances.forEach(m -> System.out.println(
                "  - [" + m.getIdMaintenance() + "] " + m.getDescription()));
        assertFalse(toutesLesMaintenances.isEmpty());

        // findById()
        Maintenance maintenanceTrouvee = maintenanceService.findById(maintenanceSauvegardee.getIdMaintenance());
        assertNotNull(maintenanceTrouvee);
        assertEquals("Vidange et contrôle freins", maintenanceTrouvee.getDescription());
        System.out.println("\nMaintenance trouvée par ID : " + maintenanceTrouvee.getDescription());

        // existsById() — simulé via findById
        boolean existe = true;
        try {
            maintenanceService.findById(maintenanceSauvegardee.getIdMaintenance());
        } catch (Exception ex) {
            existe = false;
        }
        assertTrue(existe, "La maintenance doit exister avant suppression.");
        System.out.println("La maintenance existe avant suppression : " + existe);

        // deleteById()
        maintenanceService.deleteById(maintenanceSauvegardee.getIdMaintenance());
        System.out.println("\nMaintenance supprimée (ID " + maintenanceSauvegardee.getIdMaintenance() + ").");
        assertThrows(Exception.class,
                () -> maintenanceService.findById(maintenanceSauvegardee.getIdMaintenance()));
        System.out.println("Vérification OK : la maintenance n'existe plus.");
        System.out.println("==============================================\n");
    }

    // =========================================================
    // Partie 5 — Exercice autonome : EquipementService
    // =========================================================
    @Test
    void testEquipementService() {
        System.out.println("\n========== TEST EQUIPEMENT SERVICE ==========");

        // --- Création ---
        Equipement equipement = new Equipement();
        equipement.setLibelle("GPS Navigation");

        // save()
        Equipement equipementSauvegarde = equipementService.create(equipement);
        System.out.println("Équipement enregistré : [" + equipementSauvegarde.getIdEquipement()
                + "] " + equipementSauvegarde.getLibelle());
        assertNotNull(equipementSauvegarde.getIdEquipement());
        assertEquals("GPS Navigation", equipementSauvegarde.getLibelle());

        // findAll()
        List<Equipement> tousLesEquipements = equipementService.findAll();
        System.out.println("\nListe des équipements (" + tousLesEquipements.size() + ") :");
        tousLesEquipements.forEach(e -> System.out.println(
                "  - [" + e.getIdEquipement() + "] " + e.getLibelle()));
        assertFalse(tousLesEquipements.isEmpty());

        // findById()
        Equipement equipementTrouve = equipementService.findById(equipementSauvegarde.getIdEquipement());
        assertNotNull(equipementTrouve);
        assertEquals("GPS Navigation", equipementTrouve.getLibelle());
        System.out.println("\nÉquipement trouvé par ID : " + equipementTrouve.getLibelle());

        // existsById() — simulé via findById
        boolean existe = true;
        try {
            equipementService.findById(equipementSauvegarde.getIdEquipement());
        } catch (Exception ex) {
            existe = false;
        }
        assertTrue(existe, "L'équipement doit exister avant suppression.");
        System.out.println("L'équipement existe avant suppression : " + existe);

        // deleteById()
        equipementService.deleteById(equipementSauvegarde.getIdEquipement());
        System.out.println("\nÉquipement supprimé (ID " + equipementSauvegarde.getIdEquipement() + ").");
        assertThrows(Exception.class,
                () -> equipementService.findById(equipementSauvegarde.getIdEquipement()));
        System.out.println("Vérification OK : l'équipement n'existe plus.");
        System.out.println("==============================================\n");
    }
}