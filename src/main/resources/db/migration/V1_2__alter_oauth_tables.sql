ALTER TABLE oauth2_registered_client
ADD COLUMN post_logout_redirect_uris    varchar(2000),
ADD COLUMN status                       varchar(100)        NOT NULL,
ADD COLUMN created_at                   timestamp           NOT NULL,
ADD COLUMN update_at                    timestamp           NOT NULL,
ADD COLUMN version                      BIGINT              NOT NULL DEFAULT 0;

ALTER TABLE oauth2_registered_client
ALTER COLUMN client_settings            TYPE JSONB          USING client_settings::JSONB,
ALTER COLUMN token_settings             TYPE JSONB          USING token_settings::JSONB;