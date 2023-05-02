CREATE TABLE T_STATS_USER_MONTHLY(
  user_fk                BIGINT NOT NULL,
  year                    INT NOT NULL,
  month                   INT NOT NULL,
  type                    INT NOT NULL,

  value                   BIGINT NOT NULL DEFAULT 0,

  PRIMARY KEY(user_fk, year, month, type)
) ENGINE = InnoDB;
