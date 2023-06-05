INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
    (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
;

INSERT INTO T_STORY(id, user_fk, topic_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes, readability_score) VALUES
    (1, 1, 100, 'No content', null, 'This is the toolkit library from which all other modules inherit functionality. It also includes the core facades for the Tika API.', null, null, 'en', 0, null, 11, 1, 1)
  , (2, 1, 100, 'Publish', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
  , (3, 1, 100, 'Schedule', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 0, '2018-01-30', 1430, 7, 2)
  , (4, 1, 100, 'Draft', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 0, '2018-01-30', 1430, 7, 2)
  , (99, 1, 100, 'Deleted', null, null, null, null, 'en', 1, '2015-01-30', 1200, 6, 30)
;

INSERT INTO T_STORY_CONTENT(id, story_fk, content_type, language, content, modification_date_time) VALUES
    (20, 2, 'text/plain', 'en', 'World', now())
  , (30, 3, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now())
  , (31, 3, 'application/editorjs', 'fr', '{"time":1584718404278, "blocks":[]}', '2010-10-01')
  , (32, 3, 'application/editorjs', 'es', '{"time":1584718404278, "blocks":[]}', now())
;
