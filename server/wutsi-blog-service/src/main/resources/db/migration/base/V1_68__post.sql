CREATE TABLE T_POST(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  story_fk                BIGINT NOT NULL,

  message                 VARCHAR(255),
  picture_url             TEXT,
  social_post_id          VARCHAR(60),
  channel_type            INT DEFAULT 0 NOT NULL,
  status                  INT DEFAULT 0 NOT NULL,
  scheduled_post_date_time DATETIME DEFAULT NOW(),
  post_date_time          DATETIME,
  modification_date_time  DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  FOREIGN KEY (story_fk)   REFERENCES T_STORY(id),

  PRIMARY KEY(id)
) ENGINE = InnoDB;
