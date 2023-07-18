DROP TABLE IF EXISTS T_USER_KPI;
CREATE TABLE T_USER_KPI(
   id                      BIGINT NOT NULL AUTO_INCREMENT,

   user_id                 BIGINT NOT NULL,
   type                    INT NOT NULL DEFAULT 0,
   year                    INT NOT NULL DEFAULT 0,
   month                   INT NOT NULL DEFAULT 0,
   value                   BIGINT,

   UNIQUE(user_id, type, year, month),
   PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_USER_KPI(user_id, type, year, month, value)
    SELECT S.user_fk, K.type, K.year, K.month, SUM(value)
        FROM T_STORY_KPI K JOIN T_STORY S ON K.story_id=S.id
        GROUP BY S.user_fk, K.type, K.year, K.month;
