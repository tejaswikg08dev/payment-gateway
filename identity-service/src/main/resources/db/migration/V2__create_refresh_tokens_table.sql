-- ============================================================================
-- V2__create_refresh_tokens_table.sql
-- Creates the refresh_tokens table for secure token rotation
-- ============================================================================

CREATE TABLE refresh_tokens (
                                id          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                                token       VARCHAR(512)    NOT NULL,
                                user_id     UUID            NOT NULL,
                                expires_at  TIMESTAMP       NOT NULL,
                                revoked     BOOLEAN         NOT NULL DEFAULT FALSE,
                                created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key
                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id) REFERENCES users (id)
                                        ON DELETE CASCADE,

    -- Unique constraint on token
                                CONSTRAINT uq_refresh_tokens_token UNIQUE (token)
);

-- ─── Indexes ─────────────────────────────────────────────────────────────────

-- Primary lookup: find token during refresh flow
CREATE UNIQUE INDEX idx_refresh_tokens_token ON refresh_tokens (token);

-- Secondary lookup: find all tokens for a user (bulk revocation)
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);

-- Cleanup: find expired tokens for scheduled deletion
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at)
    WHERE revoked = FALSE;

-- ─── Comments ────────────────────────────────────────────────────────────────

COMMENT ON TABLE refresh_tokens IS 'Single-use refresh tokens with rotation support';
COMMENT ON COLUMN refresh_tokens.token IS 'Opaque token value (UUID-based, not JWT)';
COMMENT ON COLUMN refresh_tokens.revoked IS 'Set to TRUE after single use or security event';