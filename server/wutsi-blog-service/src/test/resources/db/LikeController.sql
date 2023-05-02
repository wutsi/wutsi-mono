INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (2, false, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
  , (10, false, 'john.partner', 'john.partner@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
  , (11, false, 'john13', 'john13.partner@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes, readability_score) VALUES
    (1, 1, 'Story1', null, 'Sample Summary', null, null, 'en', 0, null, 11, 1, 1)
  , (2, 1, 'Story2', 'Sample Tagline', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
  , (3, 10, 'Story3', 'Sample Tagline', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
  , (4, 10, 'Story3', 'Sample Tagline', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
;

INSERT INTO T_LIKE(id, story_fk, user_fk, device_id) VALUES
    (11, 1, 2, null)
  , (12, 2, 1, 'yyyy')
  , (13, 2, 2, 'xxx')
  , (14, 1, 10, 'xxx')
  , (15, 3, 11, null)
  , (16, 3, null, 'xxx')
  , (666, 4, null, 'xxx')
;
