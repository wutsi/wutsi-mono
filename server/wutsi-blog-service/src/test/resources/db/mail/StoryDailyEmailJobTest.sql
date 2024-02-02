INSERT INTO T_USER(id, name, email, full_name, picture_url, login_count)
VALUES (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5),
       (2, 'john.smith', 'herve.tchepannou@gmail.com', 'John Smith', 'https://picture.com/login', 1),
       (3, 'not-whitelisted', 'user-not-whitelisted@gmail.com', 'John Smith', 'https://picture.com/login', 1),
       (4, 'no-email', null, 'John Smith', 'https://picture.com/login', 1),
       (5, 'alread-sent', 'already-sent@gmail.com', 'Jane Doe', 'https://picture.com/login', 1),
       (6, 'blackisted', 'blackisted@gmail.com', 'Hacker', null, 0)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time)
VALUES (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time)
VALUES (10, null, 'session-ray', null, now(), null)
;

INSERT INTO T_STORY(id, user_fk, topic_fk, status, published_date_time, title, tagline, summary, language,thumbnail_url)
VALUES (10, 1, 101, 1, '2020-02-19', 'The war in Ukraine create a new front in world domination', 'This is an exemple of tagline', 'This is summary', 'en', 'https://picsum.photos/200/300'),
       (11, 1, 101, 1, '2020-02-18', 'Encode des histoire!', null, 'This is the summary of story #11', 'fr', 'https://picsum.photos/400/400'),
       (12, 1, 100, 1, '2020-02-17', 'Putin va sur la lune!', null, 'This is the summary of story #12', 'fr', 'https://picsum.photos/400/200'),
       (13, 1, 100, 0, '2020-02-16', 'This story is not published!', null, 'This is the summary of story #13', 'fr', 'https://picsum.photos/400/300'),
       (14, 1, 100, 1, '2020-02-19', 'Story without thumbnail', null, 'This is the summary of story #14', 'fr', null),
       (15, 1, 101, 1, '2020-02-19', 'Story for my supporter!', null, 'For my donors only', 'en','https://picsum.photos/200/300'),
       (20, 2, 101, 1, '2020-02-19', 'Roger Milla marque 10 buts!', null, 'This is summary', 'fr','https://picsum.photos/300/300'),
       (30, 1, 101, 0, null, 'Sample Story', 'Sample Tagline', 'This is summary', 'en', 'https://picsum.photos/400/200')
;

-- UPDATE T_STORY set access=2 where id = 15;

INSERT INTO T_STORY_CONTENT(story_fk, content_type, language, content, modification_date_time)
VALUES (10, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now()),
       (15, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now()),
       (20, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now())
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk)
VALUES (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6)
;

INSERT INTO T_EVENT(id, stream_id, type, entity_id, user_id, device_id, version)
VALUES ('100', 7, 'urn:wutsi:blog:event:story-daily-email-sent', '10', '5', null, 1)
;

INSERT INTO T_XEMAIL(id, email, type)
VALUES ('bd92e3b9058784aabac964677a0882e3', 'blackisted@gmail.com', 2);

INSERT INTO T_STORE(id, user_fk, currency, subscriber_discount)
VALUES ('1', 1, 'XAF', 20),
       ('2', 2, 'XAF', 20),
       ('3', 3, 'XAF', 20)
;

UPDATE T_USER
set store_id='1'
where id = 1;

INSERT INTO T_PRODUCT(id, external_id, store_fk, status, title, image_url, file_url, available, price)
VALUES (101, '101', '1', 1, 'product 101', 'https://picsum.photos/1200/600', 'https://file.com/101.pdf', true, 1000),
       (102, '102', '1', 1, 'product 102', 'https://picsum.photos/1200/600', 'https://file.com/102.pdf', false, 2000),
       (103, '103', '1', 0, 'product 103', 'https://picsum.photos/800/800', 'https://file.com/102.pdf', true, 500),
       (201, '201', '2', 1, 'product 201', 'https://picsum.photos/800/800', 'https://file.com/201.pdf', true, 1500),
       (301, '301', '3', 0, 'product 301', 'https://picsum.photos/800/800', 'https://file.com/301.pdf', true, 500);


INSERT T_COUPON(product_fk, user_fk, transaction_fk, percentage, creation_date_time, expiry_date_time)
VALUES (101, 2, null, 35, now(), date_add(now(), interval 1 day));