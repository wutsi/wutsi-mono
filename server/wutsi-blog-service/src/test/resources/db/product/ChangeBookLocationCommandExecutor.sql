INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe'),
       (200, false, 'john2', 'john2.partner@gmail.com', 'Jane Doe'),
       (300, false, 'john3', 'john3.partner@gmail.com', 'Jane Doe');

INSERT INTO T_STORE(id, user_fk, currency)
VALUES ('1', 100, 'XAF'),
       ('2', 200, 'XAF'),
       ('3', 300, 'XAF');

INSERT INTO T_WALLET(id, user_fk, currency, country)
VALUES ('1', 100, 'XAF', 'cm'),
       ('2', 200, 'XAF', 'cm'),
       ('3', 300, 'XAF', 'cm');

INSERT INTO T_PRODUCT(id, external_id, store_fk, status, title, image_url, file_url, available, price)
VALUES (100, '100', '1', 1, 'product 101', 'https://picsum/101', 'https://file.com/101.pdf', true, 1000),
       (101, '101', '1', 1, 'product 102', 'https://picsum/102', 'https://file.com/102.pdf', false, 2000),
       (201, '201', '2', 1, 'product 201', 'https://picsum/201', 'https://file.com/201.pdf', true, 1500),
       (301, '301', '3', 0, 'product 301', 'https://picsum/301', 'https://file.com/301.pdf', true, 500);

INSERT INTO T_STORY(id, user_fk, title)
VALUES (1, 100, 'This is PRODUCT 103'),
       (2, 100, 'Sample Story')
;

INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, wallet_fk, user_fk, store_fk, product_fk, amount, fees,
                          net, currency, payment_method_owner, payment_method_number, payment_method_type, gateway_type,
                          description, gateway_transaction_id, email, creation_date_time)
VALUES ('100', 'pending-100', 2, 1, '1', 3, '1', 101, 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1,
        'Sample Transaction', '100-100', 'roger.milla@gmail.com', now()),
       ('101', 'success-101', 1, 1, '1', 2, '1', 101, 5000, 500, 4500, 'XAF', 'Roger Milla', '+237911111111', 1, 1,
        null, '101-100', null, date_add(now(), interval -1 day)),
       ('200', 'success-102', 1, 1, '1', 2, '1', 101, 5000, 500, 4500, 'XAF', 'Roger Milla', '+237911111111', 1, 1,
        null, '101-100', null, date_add(now(), interval -1 day))
;

INSERT INTO T_BOOK(id, product_fk, user_fk, transaction_fk)
VALUES (100, 100, 100, '100'),
       (101, 101, 100, '101'),
       (200, 201, 200, '200');
