CREATE TABLE T_STATS(
  target_fk               BIGINT NOT NULL,
  type                    INT NOT NULL,

  value                   BIGINT NOT NULL DEFAULT 0,

  PRIMARY KEY(target_fk, type)
) ENGINE = InnoDB;
