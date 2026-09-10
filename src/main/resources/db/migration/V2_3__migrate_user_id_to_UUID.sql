ALTER TABLE users ADD COLUMN new_id UUID DEFAULT uuidv7();
UPDATE users SET new_id = uuidv7() WHERE new_id IS NULL;

-- Обновляем в таблице oauth2_session
ALTER TABLE oauth2_session ADD COLUMN new_user_id UUID;
UPDATE oauth2_session os SET new_user_id = u.new_id FROM users u WHERE os.user_id = u.id;

ALTER TABLE oauth2_session DROP CONSTRAINT fk_oauth2_session_users;
ALTER TABLE oauth2_session DROP COLUMN user_id;
ALTER TABLE oauth2_session RENAME COLUMN new_user_id TO user_id;
ALTER TABLE oauth2_session ALTER COLUMN user_id SET NOT NULL;

-- Обновляем в таблице oauth2_authorization
UPDATE oauth2_authorization oa SET principal_name = u.new_id::TEXT FROM users u WHERE oa.principal_name::BIGINT = u.id;

UPDATE oauth2_authorization oa SET attributes = jsonb_set(
    oa.attributes::jsonb,
    '{java.security.Principal,principal,id}',
    to_jsonb(u.new_id::text),
    false
)::text
FROM users u
WHERE oa.attributes::jsonb #>> '{java.security.Principal,principal,id}' ~ '^[0-9]+$'
AND (oa.attributes::jsonb #>> '{java.security.Principal,principal,id}')::bigint = u.id;

-- Обновляем в таблице oauth2_authorization_consent
UPDATE oauth2_authorization_consent oac SET principal_name = u.new_id::TEXT FROM users u WHERE oac.principal_name::BIGINT = u.id;

ALTER TABLE users DROP CONSTRAINT users_pkey;
ALTER TABLE users DROP COLUMN id;
ALTER TABLE users RENAME COLUMN new_id TO id;
ALTER TABLE users ADD PRIMARY KEY (id);

ALTER TABLE oauth2_session ADD CONSTRAINT fk_oauth2_session_users
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;