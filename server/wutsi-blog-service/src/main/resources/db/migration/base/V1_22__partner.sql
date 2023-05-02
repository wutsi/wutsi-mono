CREATE TABLE T_PARTNER(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  user_fk                 BIGINT NOT NULL,

  full_name               VARCHAR(100),
  mobile_provider         INT NOT NULL,
  mobile_number           VARCHAR(30) NOT NULL,
  country_code            VARCHAR(2) NOT NULL,
  creation_date_time      DATETIME DEFAULT NOW(),
  modification_date_time  DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  FOREIGN KEY (user_fk)   REFERENCES T_USER(id),
  UNIQUE(mobile_number),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

