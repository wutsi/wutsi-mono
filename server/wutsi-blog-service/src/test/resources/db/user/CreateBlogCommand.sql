INSERT INTO T_USER(id, name, email, full_name, picture_url, website_url, login_count, biography, blog) VALUES
    (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', false),
    (2, 'roger.milla', 'roger.milla@gmail.com', 'Ray Sponsible', 'https://picture.com/roger.milla', 'https://me.com/roger.milla', 5, 'Angel investor', false),
    (100, 'user-100', 'update.wallet@gmail.com', '-', null, null, 13, null, true)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
    (10,  1, 1,   'ray.sponsible', 1, '2018-01-01'),
    (100, 1, 100, 'user-100', 1, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time) VALUES
    (10, null, 'session-ray', null, now(), null),
    (100, null, 'session-user-100', null, now(), null)
;
