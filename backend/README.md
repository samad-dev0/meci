# Frontend Angular - MECI E-SERVICES UI

Ce dossier contient l'interface utilisateur **Angular** basée sur le thème Nebular (`ngx-admin`).

## Fonctionnalités Intégrées

1. **Menu Secrétaire - Ajout Rapport** :
   - Accessible depuis le menu de navigation **Secretariat** -> **Ajout rapport** (en haut du sous-menu).
   - Permet d'ajouter un nouveau rapport, de consulter la liste des rapports, de filtrer par type/statut, de voir le détail et de supprimer un rapport.

2. **Menu Admin Rapport** :
   - Rubrique principale **Admin rapport** dans la navigation latérale.
   - **Écran d'authentification** : Demande le nom d'utilisateur et le mot de passe avant d'accéder au panneau d'administration.
   - **Supervision & Validation** : Possibilité de valider un rapport (*Validé*), le passer *En attente*, ou le *Rejeter*.
   - **Gestion Utilisateurs** : Visualisation et suppression des comptes utilisateurs.
   - **Matrice d'Autorisations de Vérification** : Grille dynamique avec boutons cases à cocher (Checkboxes) et raccourcis pour octroyer les permissions de vérification (1 vers 1, 2 vers 1, 1 vers tous, tous vers 1 ou 2 personnes).

## Prérequis

- **Node.js** (v14+ ou v16+ ou v18+)
- **npm**

## Installation et Lancement

1. Ouvrir le dossier frontend `e-service-meci-ui` dans Visual Studio Code.
2. Installer les dépendances :
   ```bash
   npm install
   ```
3. Lancer le serveur de développement Angular :
   ```bash
   npm start
   # ou
   ng serve
   ```
4. Ouvrir votre navigateur sur [http://localhost:4200](http://localhost:4200).

## Communication avec le Backend

L'URL de l'API backend est configurée dans `src/environments/environment.ts` :
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
};
```
Assurez-vous que le backend Spring Boot est lancé séparément sur le port 8080.
