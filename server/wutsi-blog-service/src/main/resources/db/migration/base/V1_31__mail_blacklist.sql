CREATE TABLE T_MAIL_UNSUBSCRIBED(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  email                   VARCHAR(255),
  creation_date_time      DATETIME NOT NULL DEFAULT now(),

  UNIQUE(email),
  PRIMARY KEY(id)
) ENGINE = InnoDB;
