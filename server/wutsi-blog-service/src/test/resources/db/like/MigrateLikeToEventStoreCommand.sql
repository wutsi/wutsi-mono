INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
    (100, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (200, false, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes, readability_score) VALUES
    (1, 100, 'Story1', null, 'Sample Summary', null, null, 'en', 0, null, 11, 1, 1)
  , (2, 200, 'Story2', 'Sample Tagline', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
;

INSERT INTO T_LIKE(story_fk, user_fk, device_id) VALUES
    (1, 100, null)
  , (2, 200, 'yyyy')
;
