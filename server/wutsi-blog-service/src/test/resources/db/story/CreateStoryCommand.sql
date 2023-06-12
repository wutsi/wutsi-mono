INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5),
    (2, false, 'roger.milla', 'roger.milla@gmail.com', 'Roger Milla', 'https://picture.com/roger.milla', 5)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
    (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time) VALUES
    (10, null, 'session-ray', null, now(), null)
;
