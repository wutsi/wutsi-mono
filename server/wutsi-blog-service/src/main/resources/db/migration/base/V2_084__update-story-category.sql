UPDATE T_STORY set category_fk=1925 WHERE topic_fk IN (select id from T_TOPIC where name='outdoor');
UPDATE T_STORY set category_fk=2213 WHERE topic_fk IN (select id from T_TOPIC where name='environment');
UPDATE T_STORY set category_fk=1311 WHERE topic_fk IN (select id from T_TOPIC where name='food');
UPDATE T_STORY set category_fk=1922 WHERE topic_fk IN (select id from T_TOPIC where name='travel');
UPDATE T_STORY set category_fk=2214 WHERE topic_fk IN (select id from T_TOPIC where name='history');

UPDATE T_STORY set category_fk=2118 WHERE topic_fk IN (select id from T_TOPIC where name='startups');
UPDATE T_STORY set category_fk=2327 WHERE topic_fk IN (select id from T_TOPIC where name='marketing');