CREATE TABLE T_PASSWORD(
  id              BIGINT NOT NULL AUTO_INCREMENT,

  account_id      BIGINT NOT NULL,
  value           VARCHAR(32) NOT NULL,
  salt            VARCHAR(36) NOT NULL,
  is_deleted      BOOL DEFAULT false,
  created         DATETIME DEFAULT NOW(),
  updated         DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted         DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;
