CREATE TABLE oauth2_authorization_consent
(
    registered_client_id        VARCHAR(100)          NOT NULL,
    principal_name              VARCHAR(200)          NOT NULL,
    authorities                 VARCHAR(1000)         NOT NULL,
    created_at                  timestamp             NOT NULL,
    update_at                   timestamp             NOT NULL,
    version                     BIGINT                NOT NULL DEFAULT 0,

    PRIMARY KEY (registered_client_id, principal_name),

    CONSTRAINT fk_oauth2_authorization_consent_registered_client
        FOREIGN KEY (registered_client_id)
        REFERENCES oauth2_registered_client (id)
        ON DELETE CASCADE
);