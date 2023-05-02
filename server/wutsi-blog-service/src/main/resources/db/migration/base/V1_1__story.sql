CREATE TABLE T_STORY(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  user_fk                 BIGINT NOT NULL,

  title                   VARCHAR(255),
  summary                 VARCHAR(255),
  thumbnail_url           TEXT,
  source_url              TEXT,
  word_count              INT NOT NULL DEFAULT 0,
  reading_minutes         INT NOT NULL DEFAULT 0,
  language                VARCHAR(2),
  status                  INT,
  creation_date_time      DATETIME DEFAULT NOW(),
  modification_date_time  DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  published_date_time     DATETIME DEFAULT NOW(),

  FOREIGN KEY (user_fk)   REFERENCES T_USER(id),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_STORY_CONTENT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,
  story_fk                BIGINT NOT NULL,

  content                 TEXT,
  content_type            VARCHAR(100),
  creation_date_time      DATETIME DEFAULT NOW(),
  modification_date_time  DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  PRIMARY KEY(id)
) ENGINE = InnoDB;
