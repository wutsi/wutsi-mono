CREATE TABLE T_DEVICE_USER(
  user_fk                 BIGINT NOT NULL,
  device_id               VARCHAR(36) NOT NULL,

  creation_date_time      DATETIME DEFAULT NOW(),
  modification_date_time  DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  PRIMARY KEY(user_fk, device_id)
) ENGINE = InnoDB;

CREATE INDEX T_DEVICE_USER__device_id ON T_DEVICE_USER(device_id);
