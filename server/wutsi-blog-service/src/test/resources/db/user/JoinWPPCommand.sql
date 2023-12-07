INSERT INTO T_USER(id, name, email, full_name, wpp)
VALUES (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', false),
       (2, 'roger.milla', 'roger.milla@gmail.com', 'Roger Milla', false)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time)
VALUES (10, 1, 1, 'ray.sponsible', 1, '2018-01-01'),
       (20, 1, 2, 'user-20', 1, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time)
VALUES (10, null, 'session-ray', null, now(), null),
       (20, null, 'session-user-20', null, now(), null)
;
