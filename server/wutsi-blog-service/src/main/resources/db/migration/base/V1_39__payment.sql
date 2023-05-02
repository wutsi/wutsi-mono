CREATE TABLE T_CONTRACT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  user_fk                 BIGINT NOT NULL,

  amount                  BIGINT NOT NULL,
  currency                VARCHAR(3) NOT NULL,
  start_date              DATE,
  end_date                DATE,
  creation_date_time      DATETIME DEFAULT NOW(),
  modification_date_time  DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  FOREIGN KEY (user_fk)   REFERENCES T_USER(id),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_EARNING (
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  user_fk                 BIGINT NOT NULL,
  contract_fk             BIGINT,
  partner_fk              BIGINT,

  year                    INT NOT NULL,
  month                   INT NOT NULL,
  amount                  BIGINT NOT NULL,
  currency                VARCHAR(3) NOT NULL,

  UNIQUE(user_fk, year, month),
  FOREIGN KEY (contract_fk)  REFERENCES T_CONTRACT(id),
  FOREIGN KEY (partner_fk)   REFERENCES T_PARTNER(id),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_COUNTRY(
  country_code VARCHAR(2),
  currency_code VARCHAR(3),

  PRIMARY KEY(country_code)
) ENGINE = InnoDB;

INSERT INTO T_COUNTRY(country_code, currency_code) VALUES
  ('CM', 'XAF')
;
