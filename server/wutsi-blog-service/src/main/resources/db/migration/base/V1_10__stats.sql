DROP TABLE T_KPI_VIEWERS;

CREATE TABLE T_STATS(
  story_fk                BIGINT NOT NULL,
  type                    INT NOT NULL,
  stats_date              DATE NOT NULL,

  value                   BIGINT NOT NULL DEFAULT 0,

  PRIMARY KEY(story_fk, type, stats_date)
) ENGINE = InnoDB;
