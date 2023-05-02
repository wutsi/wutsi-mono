ALTER TABLE T_USER ADD COLUMN last_publication_date_time DATETIME;

UPDATE T_USER, (select user_fk, max(published_date_time) as published_date_time from T_STORY where status=1  group by user_fk) as T
    SET T_USER.last_publication_date_time=published_date_time
    where T_USER.id=T.user_fk;
