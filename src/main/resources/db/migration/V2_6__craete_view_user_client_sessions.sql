CREATE VIEW v_user_client_sessions AS
SELECT
    u.id           AS user_id,
    u.email        AS user_email,
    s.sid          AS sid,
    s.client_id    AS client_id,
    s.client_name  AS client_name,
    s.last_used_at AS last_used_at
FROM users u
JOIN oauth2_session s ON s.user_id = u.id;

CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_oauth2_session_user_id ON oauth2_session (user_id);
CREATE INDEX IF NOT EXISTS idx_oauth2_session_client_name ON oauth2_session (client_name);