CREATE TABLE T_PUSH_SUBSCRIPTION(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  device_id               VARCHAR(36) NOT NULL,
  token                   TEXT NOT NULL,
  creation_date_time      DATETIME DEFAULT NOW(),
  modification_date_time  DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(device_id),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

