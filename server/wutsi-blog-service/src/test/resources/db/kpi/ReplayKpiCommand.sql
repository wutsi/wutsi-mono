INSERT INTO T_USER(id, super_user, name, email, full_name) VALUES
  (111, false, 'john111', 'john111.partner@gmail.com', 'Jane Doe'),
  (211, false, 'john200', 'john200.partner@gmail.com', 'Yo Man')
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time) VALUES
  (100, 111, 'Story100', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30'),
  (101, 111, 'Story101', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30'),
  (200, 211, 'Story200', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30')
;

INSERT INTO T_STORY_KPI(story_id, type, year, month, value)
    VALUES
        (200, 1, YEAR(now()), MONTH(now())+1, 11)
    ;
