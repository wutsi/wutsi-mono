INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog) VALUES
    (1, 2, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Illunati Blog', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true),
    (2, 0, 'jane.doe', 'login@gmail.com', 'Strickly Anonymous', 'https://picture.com/jane.doe', null, 1, null, true),
    (3, 1, 'yo', 'yo@gmail.com', 'Yo', 'https://picture.com/login.without.email', null, 1, null, false),
    (4, 1, 'foo', 'foo@gmail.com', 'Foo', null, null, 1, null, false),
    (5, 1, 'zero-balance', 'bar@gmail.com', 'Foo', null, null, 1, null, false),
    (6, 1, 'no-account-number', 'bar@gmail.com', 'Foo', null, null, 1, null, false)
;

INSERT INTO T_WALLET(id, user_fk, currency, country, balance, account_number, account_owner, account_type, next_cashout_date) VALUES
    ('1', 1, 'XAF', 'CM', 900, '+237999999991', 'Ray Sponsible', 1, now()),
    ('2', 2, 'XAF', 'CM', 1900, '+237999999992', 'Jane Doe', 1, date_sub(now(), interval 1 day)),
    ('3', 3, 'XAF', 'CM', 2000, '+237999999993', 'Yolande',  1, date_add(now(), interval 2 day)),
    ('4', 4, 'XAF', 'CM', 0, '+237999999994', 'Foo Bar',  1, null),
    ('5', 5, 'XAF', 'CM', 0, '+237999999995', 'Foo Bar',  1, now()),
    ('6', 6, 'XAF', 'CM', 0, null, 'Foo Bar',  1, now())
;
