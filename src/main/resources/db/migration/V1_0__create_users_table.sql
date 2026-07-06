CREATE SEQUENCE users_id_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE users
(
    id                  BIGINT              DEFAULT nextval('users_id_seq') PRIMARY KEY,
    email               VARCHAR(256)        NOT NULL,
    password            VARCHAR(256),
    enabled             BOOLEAN,
    external_id         text,
    provider            VARCHAR(100)        NOT NULL,
    role                VARCHAR(100)        NOT NULL,
    created_at          timestamp           NOT NULL,
    update_at           timestamp           NOT NULL,
    version             BIGINT              NOT NULL DEFAULT 0
);