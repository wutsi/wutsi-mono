DROP TABLE T_PIN;

CREATE TABLE T_PIN(
  user_fk                 BIGINT NOT NULL,
  story_fk                BIGINT NOT NULL,

  creation_date_time      DATETIME DEFAULT NOW(),

  FOREIGN KEY (user_fk)   REFERENCES T_USER(id),
  FOREIGN KEY (story_fk)  REFERENCES T_STORY(id),
  PRIMARY KEY(user_fk)
) ENGINE = InnoDB;
