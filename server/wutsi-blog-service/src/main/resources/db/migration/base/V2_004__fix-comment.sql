DROP TABLE T_COMMENT_STORY;
DROP TABLE T_COMMENT_V2;

CREATE TABLE T_COMMENT_V2(
   id                      BIGINT NOT NULL AUTO_INCREMENT,

   story_fk                BIGINT NOT NULL,
   user_fk                 BIGINT NOT NULL,
   event_fk                CHAR(36) NOT NULL,

   timestamp               DATETIME DEFAULT NOW(),

   FOREIGN KEY (user_fk)   REFERENCES T_USER(id),
   FOREIGN KEY (story_fk)  REFERENCES T_STORY(id),
   FOREIGN KEY (event_fk)  REFERENCES T_EVENT(id),
   UNIQUE(event_fk),
   PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_COMMENT_STORY(
    story_fk BIGINT NOT NULL,
    count INT DEFAULT 0,

    FOREIGN KEY (story_fk)  REFERENCES T_STORY(id),
    PRIMARY KEY(story_fk)
) ENGINE = InnoDB;

