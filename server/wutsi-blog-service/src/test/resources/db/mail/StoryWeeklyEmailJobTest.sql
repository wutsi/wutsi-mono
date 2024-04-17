INSERT INTO T_USER(id, name, email, full_name, picture_url, login_count, language, country, subscriber_count)
VALUES (10, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picsum.photos/100/100', 5, 'fr', 'cm', 10),
       (1, 'tchbansi', 'tchbansi@hotmail.com', 'Bansi T', 'https://picsum.photos/70/70', 1, 'fr', 'cm', 10),
       (2, 'htchepannou', 'herve.tchepannou.ci@gmail.com', 'Herve T', 'https://picsum.photos/70/70', 1, 'fr', 'ci', 10),
       (21, 'htchepannou', 'herve.tchepannou.sn@gmail.com', 'Herve T', 'https://picsum.photos/70/70', 1, 'fr', 'sn', 10),
       (3, 'not-whitelisted', 'user-not-whitelisted@gmail.com', 'Roger Milla', 'https://picsum.photos/100/100', 1, 'fr','cm', 1),
       (4, 'no-email', null, 'John Smith', 'https://picsum.photos/50/50', 1, 'fr', 'cm', 10),
       (5, 'alread-sent', 'already-sent@gmail.com', 'Jane Doe', 'https://picsum.photos/100/100', 1, 'fr', 'cm', 10),
       (6, 'blacklisted', 'blacklisted@gmail.com', 'Hacker', null, 0, 'fr', 'cm', 10)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time)
VALUES (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time)
VALUES (10, null, 'session-ray', null, now(), null)
;

INSERT INTO T_STORY(id, user_fk, topic_fk, status, published_date_time, title, summary, language,
                    thumbnail_url)
VALUES (10, 3, 101, 1, date_sub(now(), interval 2 day), 'The war in Ukraine create a new front in world domination','This is summary', 'en', 'https://picsum.photos/200/300'),
       (11, 4, 101, 1, date_sub(now(), interval 2 day), 'Encore des histoire!', 'This is the summary of story #11','fr', 'https://picsum.photos/400/400'),
       (12, 3, 100, 1, date_sub(now(), interval 2 day), 'Putin va sur la lune!', 'This is the summary of story #12','fr', 'https://picsum.photos/400/200'),
       (13, 3, 100, 0, date_sub(now(), interval 5 day), 'This story is not published!','This is the summary of story #13', 'fr', null),
       (14, 1, 100, 1, date_sub(now(), interval 4 day), 'Story without thumnbail', 'This is the summary of story #14','fr', null),
       (20, 6, 101, 1, date_sub(now(), interval 5 day), 'Roger Milla marque 10 buts!', 'This is summary', 'fr','https://picsum.photos/300/300'),
       (30, 6, 101, 0, null, 'Sample Story', 'This is summary', 'en', 'https://picsum.photos/400/200'),
       (100, 10, 101, 1, date_sub(now(), interval 2 day), 'Story100', 'This is the summary of story #11','fr', 'https://picsum.photos/400/400'),
       (101, 1, 101, 1, date_sub(now(), interval 2 day), 'Story101', 'This is the summary of story #11','fr', 'https://picsum.photos/400/400'),
       (102, 2, 101, 1, date_sub(now(), interval 2 day), 'Story102', 'This is the summary of story #11','fr', 'https://picsum.photos/400/400'),
       (103, 3, 101, 1, date_sub(now(), interval 2 day), 'Story103', 'This is the summary of story #11','fr', 'https://picsum.photos/400/400'),
       (104, 4, 101, 1, date_sub(now(), interval 2 day), 'Story104', 'This is the summary of story #11','fr', 'https://picsum.photos/400/400')
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk)
VALUES (6, 10);

INSERT INTO T_XEMAIL(id, email, type)
VALUES ('7ed6acf5c74f47951576a156eaccbd6d', 'blacklisted@gmail.com', 2);

INSERT INTO T_STORE(id, user_fk, currency, subscriber_discount, first_purchase_discount)
VALUES ('1', 1, 'XAF', 20, 25),
       ('2', 2, 'XAF', 0, 15),
       ('3', 3, 'XAF', 0, 5)
;

UPDATE T_USER set store_id='1' where id = 1;

INSERT INTO T_PRODUCT(id, external_id, store_fk, status, title, image_url, file_url, available, price)
VALUES (101, '101', '1', 1, 'product 101', 'https://picsum.photos/300/600', 'https://file.com/101.pdf', true, 1000),
       (102, '102', '1', 1, 'product 102', 'https://picsum.photos/400/600', 'https://file.com/102.pdf', false, 2000),
       (103, '103', '1', 0, 'product 103', 'https://picsum.photos/400/800', 'https://file.com/102.pdf', true, 500),
       (104, null, '1', 1, 'product 103', 'https://picsum.photos/800/600', 'https://file.com/102.pdf', true, 500),
       (105, null, '1', 1, 'product 103', 'https://picsum.photos/800/400', 'https://file.com/102.pdf', true, 500),
       (106, null, '1', 1, 'product 103', 'https://picsum.photos/800', 'https://file.com/102.pdf', true, 500),
       (107, null, '1', 1, 'product 103', 'https://picsum.photos/200/300', 'https://file.com/102.pdf', true, 500),
       (108, null, '1', 1, 'product 103', 'https://picsum.photos/200/300', 'https://file.com/102.pdf', true, 500),
       (201, '201', '2', 1, 'product 201', 'https://picsum.photos/400/800', 'https://file.com/201.pdf', true, 1500),
       (301, '301', '3', 0, 'product 301', 'https://picsum.photos/400/800', 'https://file.com/301.pdf', true, 500)
;

INSERT INTO T_ADS(id, user_fk, status, title, image_url, url, type, cta_type, start_date, end_date, budget, currency)
VALUES
    ('100', 6, 2, 'ads 100', 'https://picsum.photos/300/50', 'https://www.google.ca',  2, 0, now(), adddate(now(), interval 5 day ), 1000, 'XAF'),
    ('101', 6, 2, 'ads 100', 'https://picsum.photos/300/300', 'https://www.google.ca',  3, 1, '2010-01-01', null, 1000, 'XAF'),
    ('200', 6, 2, 'running', 'https://picsum.photos/300/300', 'https://www.google.ca',  3, 3, now(), adddate(now(), interval 5 day ), 1000, 'XAF'),
    ('201', 6, 2, 'ads 201', 'https://picsum.photos/300/600', 'https://www.yahoo.com',  4, 1, '2010-01-01', null, 1000, 'XAF'),
    ('202', 6, 2, 'ads 202', 'https://picsum.photos/300/600', 'https://www.yahoo.com',  4, 1, '2010-01-01', null, 1000, 'XAF')
;