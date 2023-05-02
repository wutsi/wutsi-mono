CREATE TABLE T_USER(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  email                   VARCHAR(255),
  full_name               VARCHAR(100) NOT NULL,
  picture_url             TEXT,
  login_count             BIGINT NOT NULL DEFAULT 0,
  last_login_date_time    DATETIME,
  creation_date_time      DATETIME DEFAULT NOW(),
  modification_date_time  DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;
CREATE INDEX I_USER_email ON T_USER(email);

CREATE TABLE T_ACCOUNT_PROVIDER(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  name                    VARCHAR(100),

  UNIQUE(name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_ACCOUNT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  provider_fk             BIGINT NOT NULL,
  user_fk                 BIGINT NOT NULL,

  provider_user_id        VARCHAR(30) NOT NULL,
  login_count             BIGINT NOT NULL DEFAULT 0,
  last_login_date_time    DATETIME,
  creation_date_time      DATETIME DEFAULT NOW(),
  modification_date_time  DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  FOREIGN KEY (provider_fk)   REFERENCES T_ACCOUNT_PROVIDER(id),
  FOREIGN KEY (user_fk)       REFERENCES T_USER(id),
  UNIQUE(user_fk, provider_fk),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_SESSION(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  account_fk              BIGINT NOT NULL,
  access_token            VARCHAR(255) NOT NULL,
  refresh_token           VARCHAR(255),
  login_date_time         DATETIME DEFAULT NOW(),
  logout_date_time        DATETIME,

  UNIQUE (access_token),
  FOREIGN KEY (account_fk)   REFERENCES T_ACCOUNT(id),
  PRIMARY KEY(id)
);

INSERT INTO T_ACCOUNT_PROVIDER(id, name) VALUES
    (1, 'facebook')
  , (2, 'google')
  , (3, 'linkedin')
  , (4, 'twitter')
  , (5, 'github')
;

