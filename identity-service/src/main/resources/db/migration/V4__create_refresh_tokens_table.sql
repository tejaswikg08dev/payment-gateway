-- ============================================================================
-- V4__create_refresh_tokens_table.sql
--
-- PURPOSE: Stores refresh tokens for the token rotation security pattern.
--
-- WHY THIS TABLE EXISTS:
-- Access tokens (JWTs) are stateless — we can validate them without DB lookup.
-- Refresh tokens MUST be stateful (in DB) because:
--   1. We need to REVOKE them (can't revoke a stateless JWT)
--   2. We need REUSE DETECTION (track which tokens have been consumed)
--   3. We need per-session tracking (one token per device/login)
-- ============================================================================

CREATE TABLE refresh_tokens (
    id VARCHAR(36) PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- INDEX: Token lookup (every refresh request does WHERE token = ?)
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);

-- INDEX: User lookup (bulk revocation does WHERE user_id = ?)
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);