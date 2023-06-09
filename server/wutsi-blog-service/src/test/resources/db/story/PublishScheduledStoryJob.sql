INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count, story_count, draft_story_count, publish_story_count) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5, 6, 2, 4)
;

INSERT INTO T_STORY(id, user_fk, title, status, published_date_time, scheduled_publish_date_time) VALUES
    (10, 1, 'Already published', 1, '2020-01-01',null)
  , (20, 1, 'Scheduled to be published today', 0, null, now())
  , (30, 1, 'Scheduled to be published in 1 year', 0, null, (now() + INTERVAL 1 YEAR ))
  , (31, 1, 'Scheduled to be published in 2 days', 0, null, (now() + INTERVAL 2 DAY))
  , (40, 1, 'Draft', 0, null, null)
;
