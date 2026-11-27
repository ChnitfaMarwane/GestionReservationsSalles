# Gestion des Réservations de Salles

## Introduction

**Gestion des Réservations de Salles** est une application Java Client-Serveur conçue pour gérer la réservation de salles en environnement multi-utilisateurs. Elle intègre une interface graphique **Swing** non bloquante (`SwingWorker`), une communication réseau **TCP multi-thread** (`Socket` / `ServerSocket` avec échanges d'objets sérialisés), une base de données locale **SQLite via JDBC** (initialisée automatiquement et sans mot de passe), ainsi que la gestion des fichiers (journalisation `logs.txt`, exportation `.csv` et sauvegarde binaire `.dat`).

La synchronisation côté serveur et le contrôle transactionnel garantissent qu'une même salle ne peut pas être réservée simultanément sur des créneaux horaires qui se chevauchent.

---

## Architecture et Schémas UML

<img width="5443" height="5890" alt="UML" src="https://github.com/user-attachments/assets/62cb2eee-f6b5-453d-912b-e5494229de84" />

---

## Exécution (respecter cet ordre)

Les fichiers JAR sont déjà générés et prêts à l'emploi. La base de données SQLite (`ReservationDB.db`) est créée et initialisée automatiquement au démarrage, sans mot de passe.

### Étape 1 : Lancer le serveur en premier (Terminal 1)

```bash
java -jar Serveur.jar
```

### Étape 2 : Lancer le client en deuxième (Terminal 2)

```bash
java -jar Client.jar
```

> Pour tester plusieurs utilisateurs en même temps et la gestion des conflits, lancez `java -jar Client.jar` dans plusieurs terminaux simultanément.

---

## Connexion et réservations

Une fois l'interface graphique ouverte, saisissez un email et un mot de passe parmi les comptes préconfigurés ci-dessous pour vous connecter et commencer vos réservations :

| Profil | Email | Mot de passe | Rôle | Accès |
| :--- | :--- | :--- | :--- | :--- |
| **Administrateur** | `admin@uca.ma` | `admin123` | `ADMIN` | Tableau de bord, réservations et gestion des salles |
| **Utilisateur 1** | `user1@uca.ma` | `1234` | `USER` | Tableau de bord et réservations |
| **Utilisateur 2** | `user2@uca.ma` | `1234` | `USER` | Tableau de bord et réservations |
