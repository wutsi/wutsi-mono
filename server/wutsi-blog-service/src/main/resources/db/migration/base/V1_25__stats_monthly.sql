CREATE TABLE T_STATS_MONTHLY(
  target_fk               BIGINT NOT NULL,
  year                    INT NOT NULL,
  month                   INT NOT NULL,
  type                    INT NOT NULL,

  value                   BIGINT NOT NULL DEFAULT 0,

  PRIMARY KEY(target_fk, year, month, type)
) ENGINE = InnoDB;
