INSERT INTO T_USER(id, name, email, full_name, picture_url, website_url, login_count, biography, blog, story_count, publish_story_count) VALUES
    (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true, 1, 1)
  , (2, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false, 2, 1)
  , (3, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', 'https://picture.com/login.without.email', null, 1, null, false, 3, 3)
  , (4, 'logout', 'logout@gmail.com', 'Logout', null, null, 1, null, false, 4, 0)
  , (5, 'update.account', 'update.account@gmail.com', '-', null, null, 1, null, false, 5, 0)
  , (6, 'update.user', 'update.user@gmail.com', '-', null, null, 13, null, false, 6, 0)

  , (10, 'rename.user', 'rename.update@gmail.com', '-', null, null, 13, null, false, 7, 0)
  , (11, 'duplicate.name', 'duplicate.name@gmail.com', '-', null, null, 13, null, false, 8, 0)

  , (20, 'change.email', 'change.email@gmail.com', '-', null, null, 13, null, false, 9, 0)
  , (21, 'duplicate.email', 'duplicate.email@gmail.com', '-', null, null, 13, null, false, 10, 10)

  , (30, 'update.fullname', 'update.fullname@gmail.com', '-', null, null, 13, null, false, 11, 1)

  , (40, 'update.picture', 'update.picture@gmail.com', '-', null, null, 13, null, false, 12, 0)

  , (50, 'set.wallet', 'update.wallet@gmail.com', '-', null, null, 13, null, false, 12, 0)
  , (51, 'update.wallet', 'update.wallet@gmail.com', '-', null, null, 13, null, false, 12, 0)
  , (52, 'update.wallet.duplicate-number', 'update.wallet@gmail.com', '-', null, null, 13, null, false, 12, 0)
;

UPDATE T_USER SET suspended=true WHERE id=52;
