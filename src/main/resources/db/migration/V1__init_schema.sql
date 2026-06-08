-- Roles
CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Users
CREATE TABLE users (
    id                   BIGSERIAL PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL,
    email                VARCHAR(255) NOT NULL,
    password             VARCHAR(255),
    email_verified_at    TIMESTAMP,
    auth_provider        VARCHAR(20)  NOT NULL DEFAULT 'LOCAL',
    provider_id          VARCHAR(255),
    profile_picture_url  VARCHAR(512),
    role_id              BIGINT NOT NULL,
    is_enabled           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE UNIQUE INDEX idx_users_email ON users(email);

-- Clients
CREATE TABLE clients (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL UNIQUE,
    is_actif   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_clients_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Categories
CREATE TABLE categories (
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(100) NOT NULL,
    subtitle      VARCHAR(500) NOT NULL,
    is_organized  BOOLEAN NOT NULL DEFAULT FALSE,
    is_spent      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Gains
CREATE TABLE gains (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    categorie_id  BIGINT NOT NULL,
    libelle       VARCHAR(55),
    sum           NUMERIC(15, 2) NOT NULL,
    is_recurrent  BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_gains_user      FOREIGN KEY (user_id)      REFERENCES users(id)      ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_gains_categorie FOREIGN KEY (categorie_id) REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX idx_gains_user_id      ON gains(user_id);
CREATE INDEX idx_gains_categorie_id ON gains(categorie_id);

-- Spents
CREATE TABLE spents (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    gain_id       BIGINT,
    categorie_id  BIGINT NOT NULL,
    libelle       VARCHAR(55),
    is_spent      BOOLEAN NOT NULL DEFAULT TRUE,
    value         NUMERIC(15, 2) NOT NULL,
    is_deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_spents_user      FOREIGN KEY (user_id)      REFERENCES users(id)      ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_spents_gain      FOREIGN KEY (gain_id)      REFERENCES gains(id)      ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_spents_categorie FOREIGN KEY (categorie_id) REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX idx_spents_user_id      ON spents(user_id);
CREATE INDEX idx_spents_gain_id      ON spents(gain_id);
CREATE INDEX idx_spents_categorie_id ON spents(categorie_id);

-- Refresh tokens
CREATE TABLE refresh_tokens (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    token       VARCHAR(512) NOT NULL UNIQUE,
    expires_at  TIMESTAMP NOT NULL,
    revoked     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- Verification codes
CREATE TABLE verification_codes (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL,
    code        VARCHAR(10)  NOT NULL,
    purpose     VARCHAR(30)  NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    used        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_verification_codes_email ON verification_codes(email);
