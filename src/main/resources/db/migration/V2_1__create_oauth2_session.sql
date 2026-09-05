CREATE TABLE IF NOT EXISTS oauth2_session
(
    sid                     UUID                    PRIMARY KEY DEFAULT uuidv7(),
    authorization_id        varchar(100)            NOT NULL,
    user_id                 BIGINT                  NOT NULL,
    client_id               varchar(100)            NOT NULL,
    client_name             varchar(200)            NOT NULL,
    device_id               varchar(36)             NOT NULL,
    device_ip_address       varchar(100)            NOT NULL,
    device_user_agent       varchar(255)            NOT NULL,
    last_used_at            timestamp               NOT NULL,
    created_at              timestamp               NOT NULL,
    update_at               timestamp               NOT NULL,
    version                 BIGINT                  NOT NULL DEFAULT 0,

    CONSTRAINT fk_oauth2_session_oauth2_authorization
        FOREIGN KEY (authorization_id)
        REFERENCES oauth2_authorization (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_oauth2_session_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_oauth2_session_registered_client
        FOREIGN KEY (client_id)
        REFERENCES oauth2_registered_client (id)
        ON DELETE CASCADE
);