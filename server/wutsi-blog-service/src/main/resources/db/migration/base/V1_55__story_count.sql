ALTER TABLE T_USER ADD COLUMN story_count BIGINT NOT NULL DEFAULT 0;

UPDATE T_USER U
    JOIN (SELECT user_fk, count(*) AS total_stories FROM T_STORY WHERE status=1 GROUP BY user_fk) AS T ON U.id=T.user_fk
SET U.story_count=T.total_stories
