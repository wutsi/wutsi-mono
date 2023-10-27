INSERT INTO T_USER(id, name, email, full_name, picture_url, website_url, login_count, biography, blog,
                   auto_follow_by_blogs)
VALUES (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible',
        'https://me.com/ray.sponsible', 5, 'Angel investor', false, false),
       (2, 'roger.milla', 'roger.milla@gmail.com', 'Ray Sponsible', 'https://picture.com/roger.milla',
        'https://me.com/roger.milla', 5, 'Angel investor', false, false),
       (10, 'auto-follow', null, '-', null, null, 0, null, true, true),
       (20, 'with-subscription', null, '-', null, null, 0, null, false, false),
       (21, 'with-subscription-21', null, '-', null, null, 0, null, false, false),
       (22, 'with-subscription-22', null, '-', null, null, 0, null, false, false),
       (100, 'user-100', 'update.wallet@gmail.com', '-', null, null, 13, null, true, false)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time)
VALUES (10, 1, 1, 'ray.sponsible', 1, '2018-01-01'),
       (20, 1, 20, 'user-20', 1, '2018-01-01'),
       (100, 1, 100, 'user-100', 1, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time)
VALUES (10, null, 'session-ray', null, now(), null),
       (20, null, 'session-user-20', null, now(), null),
       (100, null, 'session-user-100', null, now(), null)
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk)
VALUES (10, 100);
