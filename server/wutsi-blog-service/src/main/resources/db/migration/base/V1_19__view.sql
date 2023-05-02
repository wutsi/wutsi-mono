CREATE TABLE T_VIEW(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  story_fk                BIGINT NOT NULL,
  user_fk                 BIGINT,
  device_id               VARCHAR(36) NOT NULL,

  view_date_time          DATETIME,

  UNIQUE (story_fk, user_fk, device_id),
  PRIMARY KEY(id)
) ENGINE = InnoDB;
