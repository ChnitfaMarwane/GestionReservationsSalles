-- Structure de la base de données ReservationDB (SQLite)
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Utilisateur (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    motDePasse TEXT NOT NULL,
    role TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS Salle (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    type TEXT NOT NULL,
    capacite INTEGER NOT NULL,
    disponible INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS Reservation (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    idUtilisateur INTEGER NOT NULL,
    idSalle INTEGER NOT NULL,
    date TEXT NOT NULL,
    heureDebut TEXT NOT NULL,
    heureFin TEXT NOT NULL,
    motif TEXT NOT NULL,
    statut TEXT DEFAULT 'CONFIRMEE',
    FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (idSalle) REFERENCES Salle(id) ON DELETE CASCADE
);

-- Données de test
INSERT INTO Utilisateur (nom, email, motDePasse, role) VALUES
('Admin Principal', 'admin@uca.ma', 'admin123', 'ADMIN'),
('Etudiant 1', 'user1@uca.ma', '1234', 'USER'),
('Etudiant 2', 'user2@uca.ma', '1234', 'USER');

INSERT INTO Salle (nom, type, capacite, disponible) VALUES
('Amphi A', 'Amphithéâtre', 200, 1),
('Salle B12', 'TD', 40, 1),
('Labo Info 1', 'TP', 25, 1);

INSERT INTO Reservation (idUtilisateur, idSalle, date, heureDebut, heureFin, motif, statut) VALUES
(2, 1, '2026-12-01', '09:00', '11:00', 'Cours Java II', 'CONFIRMEE');