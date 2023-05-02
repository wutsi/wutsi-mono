INSERT INTO T_USER(id, super_user, name, email, full_name) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible')
   ,(2, false, 'john.smith', 'john.smith@gmail.com', 'John Smith')
;

INSERT INTO T_STORY(id, user_fk, title, language, status, published_date_time, word_count, reading_minutes, readability_score) VALUES
     (1, 1, 'Test1','en', 1, now(), 11, 1, 1)
    ,(2, 1, 'Test2','en', 1, now(), 11, 1, 1)
    ,(3, 1, 'Test3','en', 0, now(), 11, 1, 1)
    ,(4, 2, 'Test4','en', 1, now(), 11, 1, 1)
;
