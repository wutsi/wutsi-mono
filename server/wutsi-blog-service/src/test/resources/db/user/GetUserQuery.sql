INSERT INTO T_USER(id, name, email, full_name, picture_url, website_url, login_count, biography, blog, story_count) VALUES
    (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true, 1)
  , (2, 'jane.doe', 'jane.doe@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', 'https://me.com/jane.doe', 1, null, false, 2)
  , (3, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', 'https://picture.com/login.without.email', null, 1, null, false, 3)
  , (4, 'logout', 'logout@gmail.com', 'Logout', null, null, 1, null, false, 4)
  , (5, 'update.account', 'update.account@gmail.com', '-', null, null, 1, null, false, 5)
  , (6, 'update.user', 'update.user@gmail.com', '-', null, null, 13, null, false, 6)

  , (10, 'rename.user', 'rename.update@gmail.com', '-', null, null, 13, null, false, 7)
  , (11, 'duplicate.name', 'duplicate.name@gmail.com', '-', null, null, 13, null, false, 8)

  , (20, 'change.email', 'change.email@gmail.com', '-', null, null, 13, null, false, 9)
  , (21, 'duplicate.email', 'duplicate.email@gmail.com', '-', null, null, 13, null, false, 10)

  , (30, 'update.fullname', 'update.fullname@gmail.com', '-', null, null, 13, null, false, 11)

  , (40, 'update.picture', 'update.picture@gmail.com', '-', null, null, 13, null, false, 12)

  , (50, 'set.wallet', 'update.wallet@gmail.com', '-', null, null, 13, null, false, 12)
  , (51, 'update.wallet', 'update.wallet@gmail.com', '-', null, null, 13, null, false, 12)
  , (52, 'update.wallet.duplicate-number', 'update.wallet@gmail.com', '-', null, null, 13, null, false, 12)
;

UPDATE T_USER
    set super_user=true,
    blog=true,
    telegram_id='ray.sponsible',
    whatsapp_id='23799505555',
    last_publication_date_time=now(),
    draft_story_count=1,
    publish_story_count=2,
    story_count=3,
    subscriber_count=33,
    pin_story_id=10,
    pin_date_time=now()
WHERE id=1;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
    (10, 1, 10, 'rename.user', 1, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time) VALUES
    (10, null, 'session-10', null, now(), null)
;

INSERT INTO T_STORY(id, user_fk, topic_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes, readability_score, deleted) VALUES
    (10, 1, 101, 'Sample Story', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 0, '2018-01-30', 1430, 7, 2, false)
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk) VALUES(1,  10);
