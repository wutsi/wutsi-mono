INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (2, false, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
  , (10, false, 'john.partner', 'john.partner@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)

  , (99, true, 'ze.god', 'ze.god@gmail.com', 'Ze Got', 'https://picture.com/login', 1)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
    (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
  , (20, 1, 2, 'jane.doe', 4, '2018-01-01')
  , (99, 1, 99, 'ze.god', 4, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time) VALUES
    (10, null, 'session-ray', null, now(), null)
  , (20, null, 'session-john', null, now(), null)
  , (99, 1,'session-ze', null, now(), null)
;

INSERT INTO T_STORY(id, user_fk, status, title) VALUES
    (1, 1, 1, 'Publish'),
    (2, 1, 0, 'Draft')
;
