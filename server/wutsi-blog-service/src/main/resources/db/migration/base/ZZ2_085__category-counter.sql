UPDATE T_STORY set category_fk=2314 WHERE category_fk=1212;
UPDATE T_STORY set category_fk=2310 WHERE category_fk=1213;
UPDATE T_STORY set category_fk=2328 WHERE category_fk=1214;
UPDATE T_STORY set category_fk=2329 WHERE category_fk=1215;
UPDATE T_STORY set category_fk=2313 WHERE category_fk=1216;
UPDATE T_STORY set category_fk=2317 WHERE category_fk=1218;
UPDATE T_STORY set category_fk=2330 WHERE category_fk=1220;
UPDATE T_STORY set category_fk=2331 WHERE category_fk=1221;
UPDATE T_STORY set category_fk=2327 WHERE category_fk=1222;
DELETE FROM T_CATEGORY where parent_fk=1200;
DELETE FROM T_CATEGORY where id=1200;

DROP TABLE IF EXISTS TMP_STORY_CATEGORY;
CREATE TEMPORARY TABLE TMP_STORY_CATEGORY AS
    SELECT
        C.parent_fk as parent_id,
        C.long_title as long_title,
        S.category_fk as category_id,
        count(*) as count
    FROM
        T_STORY S JOIN T_USER U on S.user_fk=U.id
        JOIN T_CATEGORY C on S.category_fk=C.id
    WHERE S.status=1 AND U.suspended=false AND category_fk IS NOT NULL
    GROUP BY category_fk;

ALTER TABLE T_CATEGORY ADD COLUMN story_count BIGINT NOT NULL DEFAULT 0;
UPDATE T_CATEGORY C, TMP_STORY_CATEGORY T SET C.story_count=T.count WHERE C.id=T.category_id;

DROP TABLE IF EXISTS TMP_STORY_PARENT_CATEGORY;
CREATE TEMPORARY TABLE TMP_STORY_PARENT_CATEGORY AS
    SELECT
        parent_id as category_id,
        sum(count) count
    FROM TMP_STORY_CATEGORY
    GROUP BY parent_id;

UPDATE T_CATEGORY C, TMP_STORY_PARENT_CATEGORY T
    SET C.story_count=C.story_count+T.count
    WHERE C.id=T.category_id;


