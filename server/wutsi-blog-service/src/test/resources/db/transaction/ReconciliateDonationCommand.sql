INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog, super_user) VALUES
    (1, 2, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true, true),
    (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, true, false),
    (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', 'https://picture.com/login.without.email', null, 1, null, false, false)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
    (1,  1, 1,  'ray.sponsible', 1, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time) VALUES
    (1, null, 'session-ray', null, now(), null)
;

INSERT INTO T_WALLET(id, user_fk, currency, country, account_type) VALUES
    ('1', 1, 'XAF', 'CM', 1),
    ('2', 2, 'XAF', 'CM', 1)
;

INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, wallet_fk, amount, net, currency, payment_method_owner, payment_method_number, payment_method_type, gateway_type)
    VALUES
        ('100', 'donation-100', 2, 1, 1, 1000, 1000, 'XAF', 'Roger Milla', '+237911111111', 1, 1)
    ;


