ALTER TABLE oauth2_session
ADD COLUMN device_location_country      varchar(128),
ADD COLUMN device_location_city         varchar(128),
ADD COLUMN device_type                  varchar(64)         NOT NULL DEFAULT 'UNKNOWN';