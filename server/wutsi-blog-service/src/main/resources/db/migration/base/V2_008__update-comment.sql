DROP TABLE T_COMMENT_V2;

CREATE TABLE T_COMMENT_V2(
   id                      BIGINT NOT NULL AUTO_INCREMENT,

   story_fk                BIGINT NOT NULL,
   user_fk                 BIGINT NOT NULL,
   text                    TEXT NOT NULL,

   timestamp               DATETIME DEFAULT NOW(),

   FOREIGN KEY (user_fk)   REFERENCES T_USER(id),
   FOREIGN KEY (story_fk)  REFERENCES T_STORY(id),
   PRIMARY KEY(id)
) ENGINE = InnoDB;
