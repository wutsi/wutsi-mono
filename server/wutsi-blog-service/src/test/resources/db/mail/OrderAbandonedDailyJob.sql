INSERT INTO T_USER(id, name, email, full_name, picture_url, login_count)
VALUES (1, 'tchbansi', 'tchbansi@hotmail.com', 'Bansi', 'https://picture.com/ray.sponsible', 5),
       (2, 'herve.tchepannou', 'herve.tchepannou@gmail.com', 'John Smith', 'https://picture.com/login', 1),
       (10, 'Store', 'store@gmail.com', 'The Store', 'https://picsum.photos/100/100', 0)
;

INSERT INTO T_WALLET(id, user_fk, balance, currency, country) VALUES ('10', 10, 0, 'XAF', 'CM');
INSERT INTO T_STORE(id, user_fk, currency) VALUES ('10', 10, 'XAF');
UPDATE T_USER set store_id='10', wallet_id='10', facebook_id='the-store', whatsapp_id='+237911111111' where id = 10;

INSERT INTO T_PRODUCT(id, external_id, store_fk, status, title, image_url, file_url, available, price)
VALUES (100, '100', '10', 1, 'product 100', 'https://picsum.photos/1200/600', 'https://file.com/101.pdf', true, 1000),
       (110, '110', '10', 1, 'product 110', 'https://picsum.photos/1200/600', 'https://file.com/102.pdf', false, 2000),
       (103, '103', '10', 0, 'product 103', 'https://picsum.photos/800/800', 'https://file.com/102.pdf', true, 500)
;

INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, wallet_fk, store_fk, product_fk, amount, fees, net, currency, payment_method_owner, payment_method_number, payment_method_type, gateway_type, user_fk, email, creation_date_time)
VALUES
    ('100', 'failed-100', 3, 2, '10', '10', 100, 1000, 0, 0, 'XAF', 'Herve T', '+237911111111', 1, 1, 2, 'herve.tchepannou@gmail.com', '2020-10-04 08:00:00'),
    ('101', 'failed-101', 3, 2, '10', '10', 100, 1000, 0, 0, 'XAF', 'Herve T', '+237911111111', 1, 1, 2, 'herve.tchepannou@gmail.com', '2020-10-04 09:00:00'),
    ('110', 'failed-110', 3, 2, '10', '10', 110, 2000, 0, 0, 'XAF', 'Bansi', '+237911111112', 1, 1, null, 'tchbansi@hotmail.com', '2020-10-04 10:00:00'),
    ('111', 'failed-111', 1, 2, '10', '10', 110, 2000, 0, 0, 'XAF', 'Bansi', '+237911111112', 1, 1, null, 'tchbansi@hotmail.com', '2020-10-05 11:00:00'),
    ('200', 'failed-200', 3, 2, '10', '10', 100, 1000, 0, 0, 'XAF', 'Y Vois Rien', '+225911111122', 1, 1, null, 'herve.tchepannou.ci@gmail.com', '2020-10-04 08:00:00')
;

INSERT INTO T_EVENT(id, stream_id, type, entity_id, user_id, device_id, version)
VALUES ('200', 8, 'urn:wutsi:blog:event:transaction-abandoned-daily-email-sent', '200', null, null, 1)

