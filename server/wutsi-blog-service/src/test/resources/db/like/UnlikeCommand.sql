INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
  (111, false, 'john111', 'john111.partner@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes, readability_score) VALUES
  (100, 111, 'Story100', 'Sample Tagline', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2),
  (101, 111, 'Story101', 'Sample Tagline', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
;

INSERT INTO T_LIKE(story_fk, user_fk, device_id) VALUES
    (100, 111, null)
  , (100, null, 'device-unlike')
;

INSERT INTO T_LIKE_STORY(story_fk, count)
    VALUES(100, 1000);
