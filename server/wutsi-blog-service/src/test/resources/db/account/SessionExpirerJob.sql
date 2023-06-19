
INSERT INTO T_USER(id, name, email, full_name, picture_url, login_count) VALUES
    (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (2, 'login', 'login@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
;
UPDATE T_USER set super_user=true WHERE id=1;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
    (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
  , (20, 1, 2, 'jane.doe', 4, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, access_token, refresh_token, login_date_time, logout_date_time) VALUES
    (10, '100', null, DATE_ADD(now(), INTERVAL -4 DAY), now()),
    (10, '101', null, DATE_ADD(now(), INTERVAL -3 DAY), null),
    (10, '102', null, DATE_ADD(now(), INTERVAL -2 DAY), null),
    (10, '103', null, now(), null),
    (20, '200', null, now(), null)
;
