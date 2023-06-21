INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog) VALUES
    (1, 2, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true),
    (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false),
    (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', 'https://picture.com/login.without.email', null, 1, null, false)
;

INSERT INTO T_WALLET(id, user_fk, balance, currency, country) VALUES
    ('1', 1, 0, 'XAF', 'CM'),
    ('2', 2, 450, 'XAF', 'CM'),
    ('3', 3, 0, 'XAF', 'CM')
;

INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, wallet_fk, amount, fees, net, currency, payment_method_owner, payment_method_number, payment_method_type, gateway_type)
    VALUES
        ('100', 'pending-2-success', 2, 1,'1', 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1),
        ('101', 'success-101', 1, 1,'1', 5000, 500, 4500, 'XAF', 'Roger Milla', '+237911111111', 1, 1),
        ('102', 'success-102', 1, 3,'1', 1000, 0, 1000, 'XAF', 'Roger Milla', '+237911111111', 1, 1),

        ('200', 'pending-2-failed', 2, 1, '2', 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1),
        ('201', 'success-201', 1, 1, '2', 500, 50, 450, 'XAF', 'Roger Milla', '+237911111111', 1, 1),

        ('300', 'success', 1, 1, '3', 10000, 1000, 9000, 'XAF', 'Roger Milla', '+237911111111', 1, 1),
        ('310', 'failed', 3, 1, '3', 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1)
    ;
