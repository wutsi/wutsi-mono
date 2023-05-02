CREATE TABLE T_COMMENT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  user_fk                 BIGINT NOT NULL,
  story_fk                BIGINT NOT NULL,

  text                    TEXT NOT NULL,
  creation_date_time      DATETIME DEFAULT NOW(),
  modification_date_time  DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  FOREIGN KEY (user_fk)   REFERENCES T_USER(id),
  FOREIGN KEY (story_fk)  REFERENCES T_STORY(id),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

