INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (111, false, 'john111', 'john111.partner@gmail.com', 'Jane Doe'),
       (211,  false, 'john200', 'john200.partner@gmail.com', 'Yo Man'),
       (311,  false, 'john300', 'john300.partner@gmail.com', 'Ray'),
       (411, false, 'john400', 'john400.partner@gmail.com', '???')
;

INSERT INTO T_WALLET(id, user_fk, balance, currency, country)
VALUES ('1', 111, 0, 'XAF', 'CM'),
       ('2', 211, 450, 'XAF', 'CM'),
       ('3', 311, 0, 'XAF', 'CM')
;
UPDATE T_USER set wallet_id='1' where id = '111';
UPDATE T_USER set wallet_id='2' where id = '211';
UPDATE T_USER set wallet_id='3' where id = '311';


INSERT INTO T_STORY(id, user_fk, title, tagline, summary, language, status, read_count, total_duration_seconds, click_count)
VALUES (100, 111, 'Story100', 'Sample Tagline', 'This is summary', 'en', 1, 1000, 1001, 0),
       (101, 111, 'Story101', 'Sample Tagline', 'This is summary', 'en', 1, 11, 1011, 1),
       (200, 211, 'Story200', 'Sample Tagline', 'This is summary', 'en', 1, 2000, 2001, 0)
;

INSERT INTO T_STORY_KPI(story_id, type, year, month, value)
VALUES (200, 1, 2020, 3, 11),
       (200, 4, 2020, 3, 700),
       (200, 5, 2020, 3, 7)
;

INSERT INTO T_USER_KPI(user_id, type, year, month, value)
VALUES (211, 1, 2020, 2, 555),
       (211, 3, 2020, 2, 888),
       (211, 4, 2020, 2, 300),
       (211, 5, 2020, 2, 11)
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk, timestamp, story_fk)
VALUES (111, 211, '2020-02-05', 100),
       (111, 311, '2020-02-05', 100),
       (111, 411, '2020-01-05', null),

       (211, 111, '2020-02-05', null)
;

INSERT INTO T_LIKE_V2(story_fk, user_fk, device_id, timestamp)
VALUES (100, 111, null, '2020-02-05'),
       (100, null, 'device-search', '2020-02-05'),
       (101, null, 'device-search', '2020-02-05'),
       (200, null, 'device-search', '2020-02-05')
;

INSERT INTO T_COMMENT_V2(story_fk, user_fk, text, timestamp)
VALUES (100, 111, 'event-100', '2020-02-05'),
       (100, 211, 'event-100', '2020-02-05'),
       (100, 311, 'event-100', '2020-02-05'),
       (100, 411, 'event-100', '2020-02-05'),

       (101, 411, 'event-100', '2020-02-05')
;

INSERT INTO T_STORE(id, user_fk, currency, subscriber_discount, first_purchase_discount, creation_date_time)
VALUES ('1', 111, 'XAF', 10, 0, '2020-02-01'),
       ('2', 211, 'XAF', 5, 20, '2020-02-02'),
       ('3', 311, 'XAF', 0, 0, '2020-04-10');

UPDATE T_USER set store_id='1' where id = '111';
UPDATE T_USER set store_id='2' where id = '211';
UPDATE T_USER set store_id='3' where id = '311';

INSERT INTO T_PRODUCT(id, external_id, store_fk, status, title, image_url, file_url, available, price)
VALUES (101, '101', '1', 1, 'product 101', 'https://picsum/101', 'https://file.com/101.pdf', true, 1000),
       (102, '102', '1', 1, 'product 102', 'https://picsum/102', 'https://file.com/102.pdf', false, 2000),
       (103, '103', '1', 0, 'product 103', 'https://picsum/103', 'https://file.com/102.pdf', true, 500),
       (201, '201', '2', 1, 'product 201', 'https://picsum/201', 'https://file.com/201.pdf', true, 1500),
       (301, '301', '3', 0, 'product 301', 'https://picsum/301', 'https://file.com/301.pdf', true, 500);


INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, wallet_fk, store_fk, product_fk, amount, net, currency, payment_method_owner, payment_method_number, payment_method_type, gateway_type, creation_date_time)
VALUES
    ('100', 'charge-100', 1, 2, '1', '1', 101, 1000, 1000, 'XAF', 'Roger Milla', '+237911111111', 1, 1, '2020-02-05'),
    ('200', 'charge-200', 1, 2, '1', '1', 102, 2000, 2000, 'XAF', 'Song Bahanack', '+237911111100', 1, 1, '2020-02-05')
;


