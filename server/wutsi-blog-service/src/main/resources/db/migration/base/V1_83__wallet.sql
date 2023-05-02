CREATE TABLE T_WALLET(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  type                    INT NOT NULL DEFAULT 0,
  mobile_provider         INT NOT NULL DEFAULT 0,
  mobile_number           VARCHAR(30) NOT NULL,
  country                 VARCHAR(2) NOT NULL,
  full_name               VARCHAR(100) NOT NULL,
  creation_date_time      DATETIME DEFAULT NOW(),
  modification_date_time  DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(mobile_number),
  PRIMARY KEY(id)
) ENGINE = InnoDB;
