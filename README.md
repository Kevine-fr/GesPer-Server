# GesPer Server — API REST de gestion financière

> Réécriture en **Java 21 + Spring Boot 3** du backend Laravel d'origine,
> avec JWT, OAuth2 Google, BigDecimal monétaire, validation stricte
> et architecture en couches prête pour la production.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)

---

## Sommaire

- [Stack & choix techniques](#stack--choix-techniques)
- [Architecture](#architecture)
- [Démarrage rapide](#démarrage-rapide)
- [Endpoints API](#endpoints-api)
- [Authentification](#authentification)
- [Configuration OAuth2 Google](#configuration-oauth2-google)
- [Tests](#tests)
- [Docker & DevContainer](#docker--devcontainer)
- [Variables d'environnement](#variables-denvironnement)

---

## Stack & choix techniques

| Domaine                 | Technologie                                                    |
| ----------------------- | -------------------------------------------------------------- |
| Langage / build         | Java 21, Maven                                                 |
| Framework               | Spring Boot 3.3                                                |
| Sécurité                | Spring Security, JWT (jjwt), OAuth2 Google                     |
| Persistance             | Spring Data JPA / Hibernate                                    |
| Base de données         | PostgreSQL 16 (H2 en tests)                                    |
| Migrations              | Flyway                                                         |
| Validation              | Jakarta Bean Validation                                        |
| Documentation API       | Springdoc OpenAPI (Swagger UI)                                 |
| Emails                  | Spring Mail + templates Thymeleaf                              |
| Observabilité           | Spring Actuator (health, info, metrics)                        |
| Containerisation        | Docker multi-stage + docker-compose + DevContainer             |

**Pourquoi Spring Boot pour une app financière ?**

- Standard industrie (banques, fintech) — écosystème mature et audité.
- **Transactions ACID** déclaratives via `@Transactional` (cohérence des données).
- Sécurité granulaire (`@PreAuthorize`, OAuth2, JWT, BCrypt).
- **`BigDecimal` partout pour les montants** — jamais `float`/`double` qui introduisent des erreurs d'arrondi inacceptables en finance.

---

## Architecture

```
src/main/java/com/gesper/server/
├── GesperApplication.java        Point d'entrée
├── config/                       SecurityConfig, OpenApiConfig
├── common/                       Exceptions globales, ApiResponse, PageResponse, BaseEntity
├── security/
│   ├── JwtService                Génération + validation des JWT
│   ├── JwtAuthenticationFilter   Filtre HTTP → SecurityContext
│   ├── CustomUserDetails(Service)
│   ├── RestAuthenticationEntryPoint
│   └── oauth2/                   Login Google : success handler, user service
├── auth/                         Inscription, login, refresh, codes de vérification
├── user/                         Utilisateurs + clients (CRUD admin, profil)
├── categorie/                    Catégories (dépense / gain)
├── gain/                         Revenus (BigDecimal)
├── spent/                        Dépenses (BigDecimal)
└── mail/                         Envoi d'emails Thymeleaf asynchrone

src/main/resources/
├── application.yml              + profils dev/prod
├── db/migration/                V1__init_schema.sql, V2__seed_data.sql
└── templates/                   admin-mail.html, client-mail.html
```

**Pattern par couche** : `Controller (REST) → Service (logique métier, transactions) → Repository (JPA)`.
Les **DTOs** sont séparés des entités JPA (jamais d'exposition directe d'entité).

---

## Démarrage rapide

### Option A — Docker Compose (recommandé)

```bash
cp .env.example .env          # ajustez les valeurs si besoin
docker compose up --build
```

- API : <http://localhost:8080/api/v1>
- Swagger : <http://localhost:8080/api/v1/swagger-ui.html>
- MailHog (capture d'emails) : <http://localhost:8025>
- PostgreSQL : `localhost:5432` (user `gesper`, db `gesper`)

### Option B — DevContainer (VS Code)

1. Ouvrez le dossier dans VS Code avec l'extension *Dev Containers* installée.
2. *Reopen in Container*. PostgreSQL et MailHog démarrent automatiquement.
3. Lancez l'app : `mvn spring-boot:run`.

### Option C — Local sans Docker

Prérequis : Java 21, Maven 3.9+, PostgreSQL 16.

```bash
createdb gesper
cp .env.example .env
export $(grep -v '^#' .env | xargs)
mvn spring-boot:run
```

---

## Endpoints API

> Base URL : `http://localhost:8080/api/v1`
> Doc interactive complète : `GET /api/v1/swagger-ui.html`

### Authentification (public)

| Méthode | Endpoint                          | Description                                          |
| ------- | --------------------------------- | ---------------------------------------------------- |
| POST    | `/auth/send-code/admin`           | Envoie un code à l'admin global pour création admin  |
| POST    | `/auth/send-code/client`          | Envoie un code de vérification au client             |
| POST    | `/auth/register/admin?code=XXX`   | Crée un admin (nécessite un code valide)             |
| POST    | `/auth/register/client?code=XXX`  | Crée un compte client (nécessite un code valide)     |
| POST    | `/auth/login`                     | Connexion → `accessToken`, `refreshToken`            |
| POST    | `/auth/refresh`                   | Rafraîchit le token via le refresh token             |
| POST    | `/auth/logout`                    | Révoque les refresh tokens (auth requise)            |
| GET     | `/oauth2/authorize/google`        | Démarre le flow Google (`?redirect_uri=...`)         |

### Utilisateurs (authentifié)

| Méthode | Endpoint                          | Rôle requis | Description                          |
| ------- | --------------------------------- | ----------- | ------------------------------------ |
| GET     | `/users/me`                       | tout        | Profil de l'utilisateur connecté     |
| PUT     | `/users/me`                       | tout        | Met à jour son profil                |
| GET     | `/users/admin?page=0&size=20`     | ADMIN       | Liste paginée                        |
| GET     | `/users/admin/{id}`               | ADMIN       | Détail d'un utilisateur              |
| PUT     | `/users/admin/{id}`               | ADMIN       | Modifier un utilisateur              |
| DELETE  | `/users/admin/{id}`               | ADMIN       | Supprimer un utilisateur             |
| PUT     | `/users/admin/{id}/disable`       | ADMIN       | Bannir un client                     |
| PUT     | `/users/admin/{id}/enable`        | ADMIN       | Débannir un client                   |

### Catégories

| Méthode | Endpoint              | Rôle requis | Description           |
| ------- | --------------------- | ----------- | --------------------- |
| GET     | `/categories`         | tout        | Liste paginée         |
| GET     | `/categories/{id}`    | tout        | Détail                |
| POST    | `/categories`         | ADMIN       | Créer une catégorie   |
| PUT     | `/categories/{id}`    | ADMIN       | Mettre à jour         |
| DELETE  | `/categories/{id}`    | ADMIN       | Supprimer             |

### Gains (revenus)

Endpoints utilisateur (`/gains/me/...`) et admin (`/gains/admin/...`).
Les soft-deletes utilisateurs passent par `PATCH /gains/me/{id}/soft-delete`.
Voir Swagger pour le détail complet.

### Dépenses

Même découpage que les gains : `/spents/me/...` et `/spents/admin/...`.

### Format des réponses

```jsonc
// Succès
{
  "success": true,
  "message": "Récupération effectuée avec succès !",
  "data": { /* ... */ },
  "timestamp": "2025-01-15T10:23:45.123Z"
}

// Erreur
{
  "success": false,
  "status": 400,
  "message": "Données invalides",
  "errors": { "email": "Email invalide" },
  "path": "/api/v1/auth/register/client",
  "timestamp": "2025-01-15T10:23:45.123Z"
}
```

---

## Authentification

### Flow classique

```
1. POST /auth/send-code/client  { email, password }
   → email reçu avec un code à 6 chiffres
2. POST /auth/register/client?code=123456  { name, email, password }
3. POST /auth/login  { email, password }
   → { accessToken, refreshToken, expiresIn, user }
4. Toutes les requêtes suivantes : header
      Authorization: Bearer <accessToken>
5. Quand l'access token expire :
   POST /auth/refresh  { refreshToken }
   → nouveau couple (access + refresh)
```

### Sécurité

- **Hachage** : BCrypt (cost = 12).
- **Access token** : JWT signé HS256, durée par défaut **15 min**.
- **Refresh token** : token opaque stocké en base, durée par défaut **7 jours**.
  Rotation à chaque usage (l'ancien est révoqué).
- `@PreAuthorize("hasRole('ADMIN')")` sur les endpoints admin.
- Soft-delete via `@SQLDelete` + `@SQLRestriction` sur les gains et dépenses.

---

## Configuration OAuth2 Google

1. Aller sur <https://console.cloud.google.com/apis/credentials>.
2. Créer un identifiant OAuth 2.0 de type *Application Web*.
3. Ajouter comme URI de redirection autorisée :
   `http://localhost:8080/api/v1/oauth2/callback/google`
4. Renseigner dans `.env` :
   ```
   GOOGLE_CLIENT_ID=...
   GOOGLE_CLIENT_SECRET=...
   ```
5. Côté front :
   - Rediriger l'utilisateur vers
     `http://localhost:8080/api/v1/oauth2/authorize/google?redirect_uri=http://localhost:5173/oauth2/redirect`
   - À la fin du flow Google, l'utilisateur est redirigé vers `redirect_uri`
     avec `?token=<jwt>&refreshToken=<refresh>` en query params.
   - Le front les extrait et les stocke, puis utilise `Authorization: Bearer <token>`.

Seules les URIs listées dans `OAUTH2_REDIRECT_URIS` sont acceptées.

---

## Tests

```bash
mvn test
```

Les tests utilisent **H2 en mémoire** avec dialecte PostgreSQL,
et Flyway est désactivé (les tables sont créées via `ddl-auto: create-drop`).

---

## Docker & DevContainer

- **Dockerfile** (`docker/Dockerfile`) : build multi-stage, image finale `eclipse-temurin:21-jre-alpine`,
  utilisateur non-root, healthcheck via Actuator, options JVM optimisées container.
- **docker-compose.yml** : app + Postgres + MailHog en réseau dédié.
- **.devcontainer/** : environnement VS Code avec JDK 21, Maven, Postgres et MailHog.

---

## Variables d'environnement

Toutes les variables sont documentées dans [`.env.example`](.env.example).
Les principales :

| Variable                      | Description                                       |
| ----------------------------- | ------------------------------------------------- |
| `SPRING_PROFILES_ACTIVE`      | `dev` ou `prod`                                   |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | Connexion PostgreSQL                   |
| `JWT_SECRET`                  | Secret HMAC base64 (≥ 256 bits)                   |
| `JWT_ACCESS_EXPIRATION`       | TTL access token en ms (défaut : 900000)          |
| `JWT_REFRESH_EXPIRATION`      | TTL refresh token en ms (défaut : 604800000)      |
| `GOOGLE_CLIENT_ID/SECRET`     | Identifiants OAuth2 Google                        |
| `OAUTH2_REDIRECT_URIS`        | URIs front autorisées après login Google          |
| `CORS_ALLOWED_ORIGINS`        | Origins CORS autorisées                           |
| `MAIL_*`                      | SMTP                                              |

**En production**, générez un secret JWT robuste :

```bash
openssl rand -base64 64
```

---

## Différences notables avec la version Laravel

| Aspect               | Laravel d'origine                    | Version Java                                  |
| -------------------- | ------------------------------------ | --------------------------------------------- |
| Type des montants    | `float`                              | **`BigDecimal(15, 2)`** (précision financière) |
| Authentification     | Sanctum (token opaque en DB)         | JWT + refresh token rotatif                   |
| OAuth2               | absent                               | **Google login** intégré                      |
| Codes de vérification| `Cache::put(...)` mémoire            | Table dédiée avec TTL, usage unique           |
| Suppression douce    | flag `isDeleted` manuel              | `@SQLDelete` + `@SQLRestriction` (transparent)|
| Pagination           | `->limit(15)` hardcodé               | `Pageable` standard                           |
| Validation           | `$request->validate(...)`            | Annotations Bean Validation                   |
| Doc API              | absente                              | **Swagger / OpenAPI**                         |
| Architecture         | Controller-Model                     | Controller / Service / Repository / DTO       |
| Containerisation     | absente                              | Docker multi-stage + DevContainer             |

---

## Licence

MIT
