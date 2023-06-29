INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog) VALUES
    (10, 2, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true),
    (11, 2, 'ray2.sponsible', null, 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true),
    (20, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, true),
    (30, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', 'https://picture.com/login.without.email', null, 1, null, false)
;

INSERT INTO T_WALLET(id, user_fk, currency, country) VALUES
    ('10', 10, 'XAF', 'CM'),
    ('20', 20, 'XAF', 'CM'),
    ('30', 30, 'XAF', 'ZZ')
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
    (10, 1, 10, 'ray.10', 1, '2018-01-01'),
    (11, 1, 11, 'ray-11', 4, '2018-01-01'),
    (20, 1, 20, 'ray-20', 4, '2018-01-01'),
    (30, 1, 30, 'ray-30', 4, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time) VALUES
    (10, null, 'ray-10', null, now(), null),
    (11, null, 'ray-11', null, now(), null),
    (20, null, 'ray-20', null, now(), null),
    (30, null, 'ray-30', null, now(), null)
;
