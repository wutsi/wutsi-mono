CREATE TABLE T_KPI_MONTHLY(
   id                      BIGINT NOT NULL AUTO_INCREMENT,

   story_id                BIGINT NOT NULL,
   type                    INT NOT NULL DEFAULT 0,
   year                    INT NOT NULL DEFAULT 0,
   month                   INT NOT NULL DEFAULT 0,
   value                   BIGINT,

   UNIQUE(story_id, type, year, month),
   PRIMARY KEY(id)
) ENGINE = InnoDB;

ALTER TABLE T_STORY ADD COLUMN read_count BIGINT NOT NULL DEFAULT 0;
