INSERT INTO T_USER(id, subscriber_count, name, email, full_name, blog)
VALUES (1, 2, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', true),
       (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', true),
       (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', false)
;

INSERT INTO T_WALLET(id, user_fk, currency, country)
VALUES ('1', 1, 'XAF', 'CM'),
       ('2', 2, 'XAF', 'CM')
;

UPDATE T_USER
set wallet_id='1'
where id = 1;

INSERT INTO T_STORE(id, user_fk, currency)
VALUES ('100', 1, 'XAF')
;

UPDATE T_USER
set store_id='100'
where id = 1;

INSERT INTO T_PRODUCT(id, external_id, store_fk, title, description, image_url, file_url, available)
VALUES (1, '100', '100', 'product 1', 'description 1', 'https://picsum/100/100', 'https://file.com/file.pdf', true)
;

INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, wallet_fk, amount, net, currency, payment_method_owner,
                          payment_method_number, payment_method_type, gateway_type)
VALUES ('100', 'charge-100', 2, 2, 1, 1000, 1000, 'XAF', 'Roger Milla', '+237911111111', 1, 1)
;
