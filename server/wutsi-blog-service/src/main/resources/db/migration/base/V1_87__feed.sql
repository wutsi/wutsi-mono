CREATE TABLE T_FEED(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  user_fk                 BIGINT NOT NULL REFERENCES T_USER(id),
  topic_fk                BIGINT REFERENCES T_TOPIC(id),
  site_id                 BIGINT NOT NULL,
  url                     TEXT,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_ACCOUNT_PROVIDER(id, name) VALUES
    (7, 'none')
;
