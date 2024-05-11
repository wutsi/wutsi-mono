INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog)
VALUES (1, 2, 'ray.sponsible', 'herve.tchepannou@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible','https://me.com/ray.sponsible', 5, 'Angel investor', true),
       (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false),
       (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email','https://picture.com/login.without.email', null, 1, null, false)
;

INSERT INTO T_ADS(id, user_fk, title)
VALUES
    ('101', 1, 'Ads 101'),
    ('201', 1, 'Ads 201'),
    ('301', 1, 'Ads 201')
;


INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, ads_fk, amount, fees, net,currency,payment_method_owner, payment_method_number, payment_method_type, gateway_type, email)
VALUES
    ('100', 'pending-2-success', 2, 4, 101, 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1,'herve.tchepannou@gmail.com'),
    ('101', 'success-101', 1, 4, 101, 5000, 500, 4500, 'XAF', 'Roger Milla', '+237911111111', 1, 1,'herve.tchepannou@gmail.com'),
    ('102', 'pending-2-success-ebook', 2, 2, 102, 1000, 0, 1000, 'XAF', 'Roger Milla', '+237911111111',1, 1,'herve.tchepannou@gmail.com'),

    ('200', 'pending-2-failed', 2, 4, 201, 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1,'herve.tchepannou@gmail.com'),
    ('201', 'success-201', 1, 4, 201, 500, 50, 450, 'XAF', 'Roger Milla', '+237911111111', 1, 1,'herve.tchepannou@gmail.com'),

    ('300', 'success', 1, 4, 301, 10000, 1000, 9000, 'XAF', 'Roger Milla', '+237911111111', 1, 1,'herve.tchepannou@gmail.com'),
    ('310', 'failed', 3, 4, 301, 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1,'herve.tchepannou@gmail.com')
;