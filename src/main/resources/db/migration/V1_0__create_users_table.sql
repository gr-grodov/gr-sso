CREATE SEQUENCE users_id_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE users (
    id BIGINT DEFAULT nextval('users_id_seq') PRIMARY KEY,
    email VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL,
    enabled BOOLEAN
);