INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe');

INSERT INTO T_ADS(id, user_fk, status, title, image_url, url, duration_days, budget, start_date)
VALUES
    ('100', 100, 0, 'ads 100', 'https://www.img.com/1.png', 'https://www.google.ca', 5, 10000, now()),

    ('900', 100, 1, 'running', 'https://www.img.com/1.png', 'https://www.google.ca', 5, 5000, now()),
    ('901', 100, 0, 'no image', null, 'https://www.google.ca', 5, 10000, now()),
    ('902', 100, 0, 'no url', 'https://www.img.com/1.png', null, 5, 10000, now()),
    ('903', 100, 0, 'no budget', 'https://www.img.com/1.png', 'https://www.google.ca', 5, 0, now())
;