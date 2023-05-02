INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (2, false, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
  , (10, false, 'john.partner', 'john.partner@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes, readability_score) VALUES
    (1, 1, 'Test', null, 'Sample Summary', null, null, 'en', 0, null, 11, 1, 1)
  , (2, 1, 'Sample Story', 'Sample Tagline', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
;

INSERT INTO T_COMMENT(id, story_fk, user_fk, text) VALUES
    (10, 1, 2, 'Hello world')
  , (11, 1, 2, 'update me')
  , (12, 1, 2, 'delete me')

  , (20, 2, 1, 'This is a sample comment 1')
  , (21, 2, 10, 'This is a sample comment 2')
  , (22, 2, 2, 'This is a sample comment 3')
  , (23, 2, 2, 'This is a sample comment 4')
  , (24, 2, 2, 'This is a sample comment 5')
  , (25, 2, 2, 'This is a sample comment 6')
  , (26, 2, 2, 'This is a sample comment 7')
  , (27, 2, 2, 'This is a sample comment 8')
;
