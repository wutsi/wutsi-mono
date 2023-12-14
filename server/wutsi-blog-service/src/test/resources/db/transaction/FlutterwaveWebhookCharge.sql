INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog)
VALUES (1, 2, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible',
        'https://me.com/ray.sponsible', 5, 'Angel investor', true),
       (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false),
       (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email',
        'https://picture.com/login.without.email', null, 1, null, false)
;

INSERT INTO T_WALLET(id, user_fk, balance, currency, country, donation_count)
VALUES ('1', 1, 0, 'XAF', 'CM', 1),
       ('2', 2, 450, 'XAF', 'CM', 1),
       ('3', 3, 0, 'XAF', 'CM', 1)
;

UPDATE T_USER
set wallet_id='1'
where id = 1;

INSERT INTO T_STORE(id, user_fk, currency)
VALUES ('100', 1, 'XAF'),
       ('200', 2, 'XAF'),
       ('300', 3, 'XAF')
;

UPDATE T_USER
set store_id='100'
where id = 1;

INSERT INTO T_PRODUCT(id, external_id, store_fk, status, title, image_url, file_url, available, price)
VALUES (101, '101', '100', 1, 'product 101', 'https://picsum/101', 'https://file.com/101.pdf', true, 1000),
       (102, '102', '100', 1, 'product 102', 'https://picsum/102', 'https://file.com/102.pdf', false, 2000),
       (103, '103', '100', 0, 'product 103', 'https://picsum/103', 'https://file.com/102.pdf', true, 500),
       (201, '201', '200', 1, 'product 201', 'https://picsum/201', 'https://file.com/201.pdf', true, 1500),
       (301, '301', '300', 0, 'product 301', 'https://picsum/301', 'https://file.com/301.pdf', true, 500);

INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, wallet_fk, store_fk, product_fk, amount, fees, net,
                          currency,
                          payment_method_owner, payment_method_number, payment_method_type, gateway_type)
VALUES ('100', 'pending-2-success', 2, 2, '1', '100', 101, 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1),
       ('101', 'success-101', 1, 2, '1', '100', 101, 5000, 500, 4500, 'XAF', 'Roger Milla', '+237911111111', 1, 1),
       ('102', 'success-102', 1, 3, '1', '100', 102, 1000, 0, 1000, 'XAF', 'Roger Milla', '+237911111111', 1, 1),

       ('200', 'pending-2-failed', 2, 2, '2', '200', 201, 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1),
       ('201', 'success-201', 1, 2, '2', '200', 201, 500, 50, 450, 'XAF', 'Roger Milla', '+237911111111', 1, 1),

       ('300', 'success', 1, 2, '3', '300', 301, 10000, 1000, 9000, 'XAF', 'Roger Milla', '+237911111111', 1, 1),
       ('310', 'failed', 3, 2, '3', '300', 301, 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1)
;
