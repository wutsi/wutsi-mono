DROP TABLE IF EXISTS T_READER;
CREATE TABLE T_READER(
   id                      BIGINT NOT NULL AUTO_INCREMENT,

   user_id                 BIGINT NOT NULL,
   story_id                BIGINT NOT NULL,

   UNIQUE(user_id, story_id),
   PRIMARY KEY(id)
) ENGINE = InnoDB;

ALTER TABLE T_STORY ADD COLUMN subscriber_reader_count BIGINT DEFAULT 0;
