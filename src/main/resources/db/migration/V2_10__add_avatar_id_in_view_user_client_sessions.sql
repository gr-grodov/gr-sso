CREATE OR REPLACE VIEW v_user_client_sessions AS
SELECT
    u.id            AS user_id,
    u.email         AS user_email,
    s.sid           AS sid,
    s.client_id     AS client_id,
    s.client_name   AS client_name,
    s.last_used_at  AS last_used_at,
    u.avatar_id     AS user_avatar_id,
    c.avatar_id     AS client_avatar_id
FROM users u
JOIN oauth2_session s ON s.user_id = u.id
JOIN oauth2_registered_client c ON c.id = s.client_id;