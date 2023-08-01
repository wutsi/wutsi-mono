INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (2, false, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
  , (10, false, 'john.partner', 'john.partner@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)

  , (99, true, 'ze.god', 'ze.god@gmail.com', 'Ze Got', 'https://picture.com/login', 1)
;

INSERT INTO T_STORY(id, user_fk, topic_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes, readability_score, deleted) VALUES
    (10, 1, 101, 'Sample Story', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2, false)
  , (20, 2, 101, 'Sample Story', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2, false)

  , (90, 1, null, 'Delete Me', null, null, null, null, 'en', 1, '2015-01-30', 1200, 6, 30, false)
  , (99, 1, null, 'Deleted', null, null, null, null, 'en', 1, '2015-01-30', 1200, 6, 30, true)
;
