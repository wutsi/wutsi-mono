INSERT INTO T_USER(id, subscriber_count, name, email, full_name, blog)
VALUES (1, 2, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', true),
       (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', true),
       (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', false)
;

INSERT INTO T_ADS(id, user_fk, title)
VALUES
    ('100', 1, 'Ads 1')
;

INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, wallet_fk, amount, net, currency, payment_method_owner,
                          payment_method_number, payment_method_type, gateway_type)
VALUES
    ('100', 'payment-100', 2, 4, 1, 1000, 1000, 'XAF', 'Roger Milla', '+237911111111', 1, 1),
    ('200', 'payment-200', 2, 4, 1, 1000, 1000, 'XAF', 'Song Bahanack', '+237911111100', 1, 1)
;