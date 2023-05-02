CREATE TABLE T_IMAGE(
  id                  BIGINT NOT NULL AUTO_INCREMENT,

  hash                VARCHAR(32) NOT NULL,
  url                 TEXT NOT NULL,
  small_url           TEXT,
  content_type        VARCHAR(100),
  content_length      BIGINT,
  width               INT,
  height              INT,
  creation_date_time  DATETIME DEFAULT NOW(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_IMAGE_hash ON T_IMAGE(hash);
