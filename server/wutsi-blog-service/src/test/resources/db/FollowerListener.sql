INSERT INTO T_USER(id, name, email, full_name, follower_count, auto_follow_by_blogs) VALUES
    (1, 'ray.sponsible1', 'ray.sponsible1@gmail.com', 'Ray Sponsible', 0, false)
  , (2, 'ray.sponsible2', 'ray.sponsible2@gmail.com', 'Ray Sponsible', 0, false)
  , (3, 'ray.sponsible3', 'ray.sponsible3@gmail.com', 'Ray Sponsible', 0, false)
  , (4, 'ray.sponsible4', 'ray.sponsible4@gmail.com', 'Ray Sponsible', 0, false)

  , (40, 'blog40', 'blog40@gmail.com', 'Ray Sponsible', 0, true)
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes, readability_score) VALUES
    (1, 1, 'This is a story.', null, 'This is the toolkit library from which all other modules inherit functionality. It also includes the core facades for the Tika API.', null, null, 'en', 0, null, 11, 1, 1)
;

INSERT INTO T_FOLLOWER(id, user_fk, follower_user_fk, follow_date_time) VALUES
  (10,  1, 2, '2018-01-01')
, (11,  1, 3, '2018-01-01')
, (12,  1, 4, '2018-01-01')

, (20,  2, 3, '2018-01-01')
, (21,  2, 4,  '2018-02-01')
;
