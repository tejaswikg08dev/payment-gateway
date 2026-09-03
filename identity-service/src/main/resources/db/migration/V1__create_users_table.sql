-- ============================================================================
-- V1__create_users_table.sql
-- Creates the core users table for the PayFlow Identity Service
-- ============================================================================

CREATE TABLE users (
                       id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                       email           VARCHAR(255)    NOT NULL,
                       password_hash   VARCHAR(72)     NOT NULL,
                       full_name       VARCHAR(150)    NOT NULL,
                       role            VARCHAR(20)     NOT NULL DEFAULT 'USER',
                       active          BOOLEAN         NOT NULL DEFAULT TRUE,
                       created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
                       CONSTRAINT uq_users_email UNIQUE (email),
                       CONSTRAINT chk_users_role CHECK (role IN ('USER', 'MERCHANT', 'ADMIN'))
);

-- ─── Indexes ─────────────────────────────────────────────────────────────────

-- Primary lookup: find user by email during login
CREATE UNIQUE INDEX idx_users_email ON users (email);

-- Filter queries: list users by role (admin dashboard)
CREATE INDEX idx_users_role ON users (role);

-- Filter queries: list active/inactive users
CREATE INDEX idx_users_active ON users (active);

-- ─── Comments ────────────────────────────────────────────────────────────────

COMMENT ON TABLE users IS 'Core user accounts for PayFlow platform';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hash (cost factor 12) - never store plaintext';
COMMENT ON COLUMN users.role IS 'Access level: USER, MERCHANT, or ADMIN';