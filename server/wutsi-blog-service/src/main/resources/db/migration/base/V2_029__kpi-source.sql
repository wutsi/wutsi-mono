ALTER TABLE T_STORY_KPI ADD COLUMN source INT DEFAULT 0;
DROP INDEX story_id ON T_STORY_KPI;
CREATE UNIQUE INDEX story_id ON T_STORY_KPI(story_id, type, year, month, source);


ALTER TABLE T_USER_KPI ADD COLUMN source INT DEFAULT 0;
DROP INDEX user_id ON T_USER_KPI;
CREATE UNIQUE INDEX user_id ON T_USER_KPI(user_id, type, year, month, source);
