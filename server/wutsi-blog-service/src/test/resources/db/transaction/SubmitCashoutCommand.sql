INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog) VALUES
    (1, 2, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Illunati Blog', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true),
    (2, 0, 'jane.doe', 'login@gmail.com', 'Strickly Anonymous', 'https://picture.com/jane.doe', null, 1, null, true),
    (3, 1, 'yo', 'yo@gmail.com', 'Yo', 'https://picture.com/login.without.email', null, 1, null, false),
    (4, 1, 'yo', 'foo@gmail.com', 'Foo', null, null, 1, null, false)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
    (1, 1, 1, 'ray.1', 1, '2018-01-01'),
    (2, 1, 2, 'ray-2', 4, '2018-01-01'),
    (3, 1, 3, 'ray-3', 4, '2018-01-01'),
    (4, 1, 4, 'ray-4', 4, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time) VALUES
    (1, null, 'ray-1', null, now(), null),
    (2, null, 'ray-2', null, now(), null),
    (3, null, 'ray-3', null, now(), null),
    (4, null, 'ray-4', null, now(), null)
;

INSERT INTO T_WALLET(id, user_fk, currency, country, balance, account_number, account_owner, account_type) VALUES
    ('1', 1, 'XAF', 'CM', 900, '+237999999991', 'Ray Sponsible', 1),
    ('2', 2, 'XAF', 'CM', 900, '+237999999992', 'Jane Doe', 1),
    ('3', 3, 'XAF', 'CM', 900, '+237999999993', 'Yollande',  1),
    ('4', 4, 'XAF', 'CM', 0, '+237999999994', 'Foo Bar',  1)
;

INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, wallet_fk, amount, fees, net, currency, payment_method_owner, payment_method_number, payment_method_type, gateway_type)
    VALUES
        ('100', 'donation-100', 1, 1, '1', 1000, 100, 900, 'XAF', 'Roger Milla', '+237911111111', 1, 1),
        ('200', 'donation-200', 1, 1, '2', 1000, 100, 900, 'XAF', 'Roger Milla', '+237911111111', 1, 1),
        ('300', 'donation-300', 1, 1, '3', 1000, 100, 900, 'XAF', 'Roger Milla', '+237911111111', 1, 1),
        ('400', 'donation-400', 2, 1, '4', 1000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1)
    ;
