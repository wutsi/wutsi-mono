CREATE TABLE T_LIKE(
   id                      BIGINT NOT NULL AUTO_INCREMENT,

   user_fk                 BIGINT NOT NULL,
   story_fk                BIGINT NOT NULL,

   like_date_time          DATETIME DEFAULT NOW(),

   FOREIGN KEY (user_fk)   REFERENCES T_USER(id),
   FOREIGN KEY (story_fk)  REFERENCES T_STORY(id),
   PRIMARY KEY(id)
) ENGINE = InnoDB;