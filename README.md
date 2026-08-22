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
| **Lot 6** | Membres + cotisations (recouvrement) | ✅ fait |
| **Lot 7** | Rôles étendus (Trésorier/Secrétaire) + comptes | ✅ fait |
| **Lot 8** | Notifications email (bienvenue + rappels planifiés) | ✅ fait |
| **Lot 9** | Documents joints (PV/évènements), lien évènement↔PV, statuts | ✅ fait |

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

### Documents joints

Les procès-verbaux et les évènements acceptent des pièces jointes (PDF, images,
Word — 25 Mo max), servies uniquement par le backend (`/ws/document/...`), donc
protégées par le JWT. Le backend de stockage est choisi par `app.storage.provider` :

| Provider | Usage | Variables |
|---|---|---|
| `local` (défaut) | développement : disque | `STORAGE_BASE_DIR` (défaut `<dossier utilisateur>/portail-ensi/documents`) |
| `b2` | production : Backblaze B2 (API compatible S3) | `B2_ENDPOINT`, `B2_BUCKET`, `B2_ACCESS_KEY`, `B2_SECRET_KEY`, `B2_REGION` |

En local, les fichiers vivent **hors du dépôt**, sous le dossier de l'utilisateur :
`~/portail-ensi/documents/pv/…` et `~/portail-ensi/documents/evenement/…`.
Sur B2 c'est la même arborescence, en clés d'objet dans le bucket (`pv/…`,
`evenement/…`) : aucun préfixe supplémentaire à configurer.

L'endpoint et la région B2 vont de pair : `https://s3.us-west-004.backblazeb2.com`
⟶ `us-west-004`. Le bucket reste **privé** : aucune URL de stockage n'est exposée
au navigateur. En production, ne pas rester sur `local` — le disque des
hébergeurs gratuits (Render) est éphémère et les fichiers seraient perdus à
chaque déploiement.

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
| tresorier | tresorier123 | TRESORIER |
| secretaire | secretaire123 | SECRETAIRE |
| visiteur1 | visiteur123 | VIEWER |
| visiteur2 | visiteur123 | VIEWER |

Matrice des accès (écriture) : **Trésorier** → finances + cotisations ·
**Secrétaire** → planning + procès-verbaux + membres · **Admin** → tout (dont
gestion des comptes) · **Visiteur** → lecture seule.
