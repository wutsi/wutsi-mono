INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (2, false, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
  , (10, false, 'john.partner', 'john.partner@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)

  , (99, true, 'ze.god', 'ze.god@gmail.com', 'Ze Got', 'https://picture.com/login', 1)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
    (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time) VALUES
    (10, null, 'session-ray', null, now(), null)
;

INSERT INTO T_STORY(id, user_fk, topic_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes, readability_score, deleted) VALUES
    (10, 1, 101, 'Sample Story', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2, false)
  , (20, 1, 101, 'Sample Story', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2, false)

  , (90, 1, null, 'Delete Me', null, null, null, null, 'en', 1, '2015-01-30', 1200, 6, 30, false)
  , (99, 1, null, 'Deleted', null, null, null, null, 'en', 1, '2015-01-30', 1200, 6, 30, true)
;

INSERT INTO T_STORY_CONTENT(story_fk, content_type, language, content, modification_date_time) VALUES
    (10, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now()),
    (20, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now())
;

INSERT INTO T_TAG(id, name, display_name, total_stories) VALUES
    (1, 'covid-19', 'COVID-19', 100)
  , (2, 'github', 'Github', 1)
  , (3, 'git', 'Git', 102)
  , (4, 'gitflow', 'GitFlow', 7)
  , (5, 'test', 'Test', 0)
  , (6, 'computer', 'Computer', 0)
;

INSERT INTO T_STORY_TAG(story_fk, tag_fk) VALUES
    (10, 1)
  , (10, 4)
;

UPDATE T_USER SET pin_story_id=10 WHERE id=1;
UPDATE T_STORY SET
    like_count=11,
    comment_count=22,
    share_count=33
WHERE id=20;

INSERT INTO T_COMMENT_V2(story_fk, user_fk, text) VALUES (20, 1, 'Hello man');
INSERT INTO T_LIKE_V2(story_fk, user_fk) VALUES (20, 1);
INSERT INTO T_SHARE(story_fk, user_fk) VALUES (20, 1);
