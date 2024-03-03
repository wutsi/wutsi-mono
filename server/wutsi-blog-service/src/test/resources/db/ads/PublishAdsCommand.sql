INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe');

INSERT INTO T_ADS(id, user_fk, status, title, image_url, url, budget, start_date, end_date)
VALUES
    ('100', 100, 0, 'ads 100', 'https://www.img.com/1.png', 'https://www.google.ca', 10000, now(), date_add(now(), interval 5 day )),

    ('900', 100, 2, 'not-published', 'https://www.img.com/1.png', 'https://www.google.ca', 5000, now(), date_add(now(), interval 5 day )),
    ('901', 100, 0, 'no image', null, 'https://www.google.ca', 10000, now(), date_add(now(), interval 5 day )),
    ('902', 100, 0, 'no url', 'https://www.img.com/1.png', null,  10000, now(), date_add(now(), interval 5 day )),
    ('903', 100, 0, 'no budget', 'https://www.img.com/1.png', 'https://www.google.ca',  0, now(), date_add(now(), interval 5 day )),
    ('904', 100, 0, 'no end date', 'https://www.img.com/1.png', 'https://www.google.ca', 10000, now(), null),
    ('905', 100, 0, 'start = end date', 'https://www.img.com/1.png', 'https://www.google.ca', 10000, now(), now()),
    ('906', 100, 0, 'start > end date', 'https://www.img.com/1.png', 'https://www.google.ca', 10000, now(), DATE_SUB(now(), interval 5 day )),
    ('907', 100, 0, 'no start date', 'https://www.img.com/1.png', 'https://www.google.ca', 10000, null, now())
;