CREATE TABLE T_EVENT(
  id                      CHAR(36) NOT NULL,

  type                    VARCHAR(255) NOT NULL,
  stream_id               BIGINT NOT NULL,
  entity_id               VARCHAR(36) NOT NULL,
  user_id                 BIGINT,
  device_id               VARCHAR(36),
  version                 BIGINT,
  payload                 TEXT,
  metadata                TEXT,
  timestamp               DATETIME DEFAULT NOW(),

  PRIMARY KEY(id),
  UNIQUE(stream_id, version)
) ENGINE = InnoDB;
CREATE INDEX I_EVENT_stream_entity ON T_EVENT(stream_id, entity_id);
