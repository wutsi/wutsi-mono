CREATE TABLE T_TAG(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  name                    VARCHAR(100) NOT NULL,
  total_stories           BIGINT DEFAULT 0,
  creation_date_time      DATETIME DEFAULT NOW(),

  UNIQUE(name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_STORY_TAG(
  story_fk              BIGINT NOT NULL,
  tag_fk                BIGINT NOT NULL,

  PRIMARY KEY(story_fk, tag_fk)
) ENGINE = InnoDB;

INSERT INTO T_TAG(name) VALUES
    ('COVID-19')
  , ('Actualités')
  , ('Affaires')
  , ('Sport')
  , ('Arts')
  , ('Cinéma')
  , ('Sociéte')
  , ('Voyage')
  , ('Environement')
  , ('Sciences')
  , ('Techno')
  , ('Insolite')
  , ('Politique')
  , ('Footbal')
;
