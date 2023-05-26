CREATE TABLE T_LIKE_V2(
   id                      BIGINT NOT NULL AUTO_INCREMENT,

   story_fk                BIGINT NOT NULL,
   user_fk                 BIGINT,
   device_id               VARCHAR(36),

   timestamp               DATETIME DEFAULT NOW(),

   FOREIGN KEY (user_fk)   REFERENCES T_USER(id),
   FOREIGN KEY (story_fk)  REFERENCES T_STORY(id),
   UNIQUE(story_fk, device_id),
   UNIQUE(story_fk, user_fk),
   PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_LIKE_STORY(
    story_fk BIGINT NOT NULL,
    count INT DEFAULT 0,

    FOREIGN KEY (story_fk)  REFERENCES T_STORY(id),
    PRIMARY KEY(story_fk)
)
