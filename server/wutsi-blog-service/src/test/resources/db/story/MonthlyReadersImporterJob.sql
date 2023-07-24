INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
  (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5),
  (2, false, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1),
  (10, false, 'john.partner', 'john.partner@gmail.com', 'Jane Doe', 'https://picture.com/login', 1),
  (99, true, 'ze.god', 'ze.god@gmail.com', 'Ze Got', 'https://picture.com/login', 1)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
  (10, 1, 1, 'ray.sponsible', 1, '2018-01-01'),
  (20, 1, 2, 'jane.doe', 4, '2018-01-01'),
  (99, 1, 99, 'ze.god', 4, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time) VALUES
  (10, null, 'session-ray', null, now(), null),
  (20, null, 'session-john', null, now(), null),
  (99, 1,'session-ze', null, now(), null)
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, source_url_hash, language, status, published_date_time, word_count, reading_minutes, readability_score, deleted_date_time) VALUES
  (100, 1, 'Already-Imported', null, null, 'https://www.img.com/21.png', 'https://nkowa.com/podcast-jokkolabs-douala-les-valeurs-qui-nous-tiennent-a-coeur-sont-la-culture-lagriculture-et-linnovation', 'bac453c63189f63093efe20cc991b127', 'en', 1, '2015-01-30', 1200, 6, 30, null),
  (90, 1, 'Delete Me', null, null, null, null, null, 'en', 1, '2015-01-30', 1200, 6, 30, null),
  (99, 1, 'Deleted', null, null, null, null, null, 'en', 1, '2015-01-30', 1200, 6, 30, now())
;

INSERT INTO T_STORY_CONTENT(id, story_fk, content_type, language, content, modification_date_time) VALUES
  (1000, 100, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now())
;
