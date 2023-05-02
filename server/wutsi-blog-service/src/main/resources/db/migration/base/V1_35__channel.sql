CREATE TABLE T_CHANNEL(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  user_fk                 BIGINT NOT NULL,

  type                    INT NOT NULL,
  name                    VARCHAR(100) NOT NULL,
  access_token            VARCHAR(255) NOT NULL,
  picture_url             VARCHAR(255),
  creation_date_time      DATETIME DEFAULT NOW(),
  modification_date_time  DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  FOREIGN KEY (user_fk)   REFERENCES T_USER(id),
  UNIQUE(user_fk, type),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

