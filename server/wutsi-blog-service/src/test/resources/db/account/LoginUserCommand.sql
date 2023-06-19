
INSERT INTO T_USER(id, name, email, full_name, picture_url, language) VALUES
    (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'en')
  , (2, 'login', 'login@gmail.com', 'Jane Doe', 'http://localhost:0/storage/image/upload/v1312461204/sample.jpg', 'fr')
  , (3, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', 'https://picture.com/login.without.email', null)
  , (4, 'logout', 'logout@gmail.com', 'Logout', null, null)
  , (5, 'update.account', 'update.account@gmail.com', '-', null, null)
  , (6, 'update.user', 'update.user@gmail.com', '', null, null)
  , (7, 'twitter.user', null, 'Twitter User', null, null)
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
    (10, '827c7013-f7ce-4238-947c-26fba6378d2d', '827c7013-f7ce-4238-947c-26fba6378dff', now(), null)
  , (40, 'logout', 'logout-refresh', now(), null)
  , (40, 'logout-expired', 'logout-expired-refresh', now(), now())
  , (50, '827c7013-f7ce-4238-947c-26fba6378d2f', null, now(), null)
;

INSERT INTO T_CHANNEL(id, user_fk, type, provider_user_id, name, access_token, picture_url) VALUES
    (7, 7, 2, '7777', 'FB', 'tw-7777', 'https://img.com/fb-000010.png')
;

INSERT INTO T_EVENT(id, stream_id, version, entity_id, user_id, type) VALUES
    ('1', 9, 1, '30493049039', '2', 'urn:wutsi:blog:event:user-logged-in')
;
