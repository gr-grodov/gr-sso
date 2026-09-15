CREATE TABLE IF NOT EXISTS attachments
(
    id                      UUID                    PRIMARY KEY DEFAULT uuidv7(),
    original_name           varchar(255)            NOT NULL,
    content_type            VARCHAR(100)            NOT NULL,
    size                    BIGINT                  NOT NULL,
    storage_key             varchar(200)            NOT NULL,
    storage_type            varchar(36)             NOT NULL,
    status                  varchar(36)             NOT NULL,
    created_at              timestamp               NOT NULL,
    update_at               timestamp               NOT NULL,
    version                 BIGINT                  NOT NULL DEFAULT 0
);

CREATE INDEX idx_attachments_cleanup ON attachments (status, created_at);