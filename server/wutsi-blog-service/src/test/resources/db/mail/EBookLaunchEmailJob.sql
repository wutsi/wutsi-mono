INSERT INTO T_USER(id, name, email, full_name, picture_url, login_count, country, language, facebook_id, whatsapp_id, twitter_id)
VALUES (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picsum.photos/600', 5, 'cm', 'en', 'ray.sponsible', '+2376611111', 'ray'),
       (2, 'john.smith', 'herve.tchepannou@gmail.com', 'John Smith', 'https://picture.com/login', 1, 'cm', 'fr', null, null, null),
       (3, 'not-whitelisted', 'user-not-whitelisted@gmail.com', 'John Smith', 'https://picture.com/login', 1, 'cm', 'fr', null, null, null),
       (4, 'no-email', null, 'John Smith', 'https://picture.com/login', 1, 'cm', 'fr', null, null, null),
       (5, 'alread-sent', 'already-sent@gmail.com', 'Jane Doe', 'https://picture.com/login', 1, 'cm', 'fr', null, null, null),
       (6, 'blackisted', 'blackisted@gmail.com', 'Hacker', null, 0, 'cm', 'fr', null, null, null)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time)
VALUES (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time)
VALUES (10, null, 'session-ray', null, now(), null)
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk)
VALUES (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6)
;

INSERT INTO T_EVENT(id, stream_id, type, entity_id, user_id, device_id, version)
VALUES ('100', 13, 'urn:wutsi:blog:event:product-ebook-launch-email-sent', '100', '5', null, 1)
;

INSERT INTO T_XEMAIL(id, email, type)
VALUES ('bd92e3b9058784aabac964677a0882e3', 'blackisted@gmail.com', 2);

INSERT INTO T_STORE(id, user_fk, currency, subscriber_discount, enable_donation_discount)
VALUES ('1', 1, 'XAF', 20, true);

UPDATE T_USER set store_id='1' where id = 1;

INSERT INTO T_PRODUCT(id, external_id, store_fk, type, status, title, image_url, file_url, available, price, published_date_time)
VALUES (101, '101', '1', 1, 1, 'Smaller Wardrobe, Better life', 'https://picsum.photos/600/1200', 'https://file.com/101.pdf', true, 1000, '2020-02-19'),
       (102, '102', '1', 0, 1, 'product 102', 'https://picsum.photos/1200/600', 'https://file.com/102.pdf', false, 2000, '2020-02-19'),
       (103, '103', '1', 1, 0, 'product 103', 'https://picsum.photos/800/800', 'https://file.com/102.pdf', true, 500, null),
       (104, null, '1', 1, 1, 'product 103', 'https://picsum.photos/800/600', 'https://file.com/102.pdf', true, 500, '2010-02-19'),
       (105, null, '1', 1, 1, 'product 103', 'https://picsum.photos/800/400', 'https://file.com/102.pdf', true, 500, '2010-02-19'),
       (106, null, '1', 1, 1, 'product 103', 'https://picsum.photos/800', 'https://file.com/102.pdf', true, 500, '2010-02-19'),
       (107, null, '1', 1, 1, 'product 103', 'https://picsum.photos/200/300', 'https://file.com/102.pdf', true, 500, '2010-02-19'),
       (108, null, '1', 1, 1, 'product 103', 'https://picsum.photos/200/300', 'https://file.com/102.pdf', true, 500, '2010-02-19'),
       (201, '201', '2', 1, 1, 'product 201', 'https://picsum.photos/800/800', 'https://file.com/201.pdf', true, 1500, '2010-02-19'),
       (301, '301', '3', 1, 0, 'product 301', 'https://picsum.photos/800/800', 'https://file.com/301.pdf', true, 500, null);