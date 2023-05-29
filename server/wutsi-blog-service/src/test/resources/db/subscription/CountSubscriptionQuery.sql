INSERT INTO T_USER(id, name, email, full_name, picture_url, website_url, login_count, biography, blog) VALUES
    (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true)
  , (2, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false)
  , (3, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', 'https://picture.com/login.without.email', null, 1, null, false)
  , (4, 'logout', 'logout@gmail.com', 'Logout', null, null, 1, null, false)
  , (5, 'update.account', 'update.account@gmail.com', '-', null, null, 1, null, false)
  , (6, 'update.user', 'update.user@gmail.com', '-', null, null, 13, null, false)

  , (10, 'rename.user', 'rename.update@gmail.com', '-', null, null, 13, null, false)
  , (11, 'duplicate.name', 'duplicate.name@gmail.com', '-', null, null, 13, null, false)

  , (20, 'change.email', 'change.email@gmail.com', '-', null, null, 13, null, false)
  , (21, 'duplicate.email', 'duplicate.email@gmail.com', '-', null, null, 13, null, false)

  , (30, 'update.fullname', 'update.fullname@gmail.com', '-', null, null, 13, null, false)
  , (31, 'update.fullname', 'update.fullname31@gmail.com', '-', null, null, 13, null, false)
  , (32, 'update.fullname', 'update.fullname32@gmail.com', '-', null, null, 13, null, false)

  , (40, 'update.picture', 'update.picture@gmail.com', '-', null, null, 13, null, false)
  , (41, 'update.picture', 'update.picture41@gmail.com', '-', null, null, 13, null, false)
  , (42, 'update.picture', 'update.picture42@gmail.com', '-', null, null, 13, null, false)
;


INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk) VALUES
  (1,  10),
  (1,  20),
  (1,  30),
  (1,  40),

  (2,  10),

  (3,  11),
  (3,  20),
  (3,  30)
;

INSERT INTO T_SUBSCRIPTION_USER(user_fk, count) VALUES
    (1, 4),
    (2, 1),
    (3, 3)
;
