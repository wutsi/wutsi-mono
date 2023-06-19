
INSERT INTO T_USER(id, name, email, full_name, picture_url, login_count) VALUES
    (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (2, 'login', 'login@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
  , (3, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', 'https://picture.com/login.without.email', 1)
  , (4, 'logout', 'logout@gmail.com', 'Logout', null, 1)
  , (5, 'update.account', 'update.account@gmail.com', '-', null, 1)
  , (6, 'update.user', 'update.user@gmail.com', '', null, 13)
  , (7, 'twitter.user', null, 'Twitter User', null, 13)
;
UPDATE T_USER set super_user=true WHERE id=1;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
    (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
  , (20, 1, 2, 'jane.doe', 4, '2018-01-01')
  , (30, 1, 3, 'login.without.email', 1, '2018-01-01')
  , (40, 1, 4, 'logout', 1, '2018-01-01')
  , (50, 1, 5, 'fb_update_account', 11, null)
  , (60, 1, 6, 'fb_update_user', 7, null)
;

INSERT INTO T_SESSION(account_fk, access_token, refresh_token, login_date_time, logout_date_time) VALUES
    (10, '827c7013-f7ce-4238-947c-26fba6378d2d', null, now(), null)
  , (40, '827c7013-f7ce-4238-947c-26fba6378d2f', null, now(), now())
;

INSERT INTO T_CHANNEL(id, user_fk, type, provider_user_id, name, access_token, picture_url) VALUES
    (7, 7, 2, '7777', 'FB', 'tw-7777', 'https://img.com/fb-000010.png')
;
