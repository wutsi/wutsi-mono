INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog)
VALUES (1, 2, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible',
        'https://me.com/ray.sponsible', 5, 'Angel investor', true),
       (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false),
       (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email',
        'https://picture.com/login.without.email', null, 1, null, false)
;

INSERT INTO T_WALLET(id, user_fk, balance, currency, country)
VALUES ('1', 1, 0, 'XAF', 'CM'),
       ('2', 2, 450, 'XAF', 'CM'),
       ('3', 3, 0, 'XAF', 'CM')
;


INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, wallet_fk, user_fk, amount, fees, net, currency,
                          payment_method_owner, payment_method_number, payment_method_type, gateway_type, description,
                          gateway_transaction_id, email)
VALUES ('100', 'pending-100', 2, 1, '1', 3, 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1,
        'Sample Transaction', '100-100', 'roger.milla@gmail.com'),
       ('101', 'success-101', 1, 1, '1', 3, 5000, 500, 4500, 'XAF', 'Roger Milla', '+237911111111', 1, 1, null,
        '101-100', null),
       ('102', 'success-102', 1, 3, '1', 3, 1000, 0, 1000, 'XAF', 'Roger Milla', '+237911111111', 1, 1, null, '102-100',
        null),

       ('110', 'pending-free-110', 2, 1, '1', 3, 0, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 7,
        'Sample Transaction', '110-100', 'roger.milla@gmail.com'),
       ('111', 'success-free-111', 1, 1, '1', 3, 0, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 7,
        'Sample Transaction', '111-100', 'roger.milla@gmail.com'),

       ('200', 'pending-200', 2, 1, '2', 3, 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1,
        'Sample Transaction', '200', 'roger.milla@gmail.com'),
       ('201', 'pending-201', 2, 1, '2', 3, 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1,
        'Sample Transaction', '201', 'roger.milla@gmail.com'),

       ('300', 'success-300', 1, 1, '3', 3, 50000, 5000, 48000, 'XAF', 'Roger Milla', '+237911111111', 1, 1, null,
        '300', null),
       ('310', 'failed-310', 3, 1, '3', 3, 50000, 5000, 48000, 'XAF', 'Roger Milla', '+237911111111', 1, 1, null, '301',
        null)
;
