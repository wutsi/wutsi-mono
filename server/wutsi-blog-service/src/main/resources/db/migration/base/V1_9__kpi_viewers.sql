CREATE TABLE T_KPI_VIEWERS(
  story_fk                BIGINT NOT NULL,
  kpi_date                DATE NOT NULL,

  value                   BIGINT NOT NULL DEFAULT 0,

  PRIMARY KEY(story_fk, kpi_date)
) ENGINE = InnoDB;


DELETE FROM T_STORY_CONTENT WHERE story_fk NOT IN (SELECT id from T_STORY);
ALTER TABLE T_STORY_CONTENT ADD FOREIGN KEY (story_fk)  REFERENCES T_STORY(id);
