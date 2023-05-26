CREATE TABLE T_PIN_STORY(
   user_fk                 BIGINT NOT NULL AUTO_INCREMENT,
   story_fk                BIGINT NOT NULL,
   timestamp               DATETIME DEFAULT NOW(),

   FOREIGN KEY (user_fk)   REFERENCES T_USER(id),
   FOREIGN KEY (story_fk)  REFERENCES T_STORY(id),
   UNIQUE(story_fk, user_fk),
   PRIMARY KEY(user_fk)
) ENGINE = InnoDB;
