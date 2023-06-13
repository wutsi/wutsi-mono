UPDATE T_USER SET last_publication_date_time=NULL WHERE story_count=0;
UPDATE T_USER U SET last_publication_date_time=(SELECT max(published_date_time) FROM T_STORY S WHERE status=1 AND U.id=S.user_fk);
UPDATE T_USER SET active=false WHERE last_publication_date_time < DATE_SUB(now(), INTERVAL 6 MONTH);
