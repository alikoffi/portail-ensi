# Portail ENSI — Plateforme associative

Plateforme de gestion associative de la promotion ENSI (INPHB Yamoussoukro).
Réécriture **Spring Boot + Angular** de la plateforme Challenge ENSI.

## Stack

| Couche | Technologie |
|---|---|
| Backend | Spring Boot 3.2 / Java 17 / Gradle |
| Base de données | PostgreSQL + Flyway |
| Sécurité | JWT (stateless) + BCrypt |
| Frontend | Angular 19 (standalone) + PrimeNG + Tailwind |
| Doc API | OpenAPI / Swagger |

## Architecture backend (en couches)

```
controller   → expose /ws/<entite>/{lister, enregistrer, modifier, supprimer/{id}}
facade       → @Service + @Transactional (règles métier)
repository   → Spring Data JPA
domain       → entités JPA (extends AbstractEntity : audit + @Version)
presentation.dto → DTOs
security · configuration · exception · enums · utils
```

## Découpage en lots

| Lot | Contenu | État |
|---|---|---|
| **Lot 0** | Socle : auth JWT, layout, dashboard, 6 tables | ✅ fait |
| **Lot 1** | Tableau de bord dynamique (indicateurs) | ✅ fait |
| **Lot 2** | Planning (CRUD évènements) | ✅ fait |
| **Lot 3** | États financiers (transactions + bilan) | ✅ fait |
| **Lot 4** | Procès-verbaux (CRUD) | ✅ fait |
| **Lot 5** | Graphiques (Chart.js) + export PDF | ✅ fait |

## Démarrage local

### Pré-requis
- Java 17, Node 22, PostgreSQL 16

### Base de données
```sql
CREATE DATABASE portail_ensi OWNER portail_ensi;
```
Paramètres surchargeables par variables d'environnement : `DB_HOST`, `DB_PORT`,
`DB_NAME`, `DB_USER`, `DB_PASSWORD`, `SERVER_PORT`, `JWT_SECRET`, `CLIENT_URL`,
`SPRING_PROFILE`.

Le profil `dev` (par défaut) charge des données de démonstration via Flyway
(`flyway/dev`). En production (`SPRING_PROFILE=prod`), seul le schéma est appliqué.

### Backend
```bash
cd backend
./gradlew bootRun        # http://localhost:8081  (Swagger : /swagger-ui.html)
```

### Frontend
```bash
cd frontend
npm install
npm start                # http://localhost:4200
```

## Comptes par défaut (profil dev — à changer en production)

| Login | Mot de passe | Rôle |
|---|---|---|
| admin | admin123 | ADMIN |
| visiteur1 | visiteur123 | VIEWER |
| visiteur2 | visiteur123 | VIEWER |
