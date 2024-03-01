INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES
    (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe'),
    (200, false, 'john2', 'john2.partner@gmail.com', 'Jane Doe');

INSERT INTO T_ADS(id, user_fk, status, title, image_url, url, duration_days, type, cta_type, start_date, end_date, budget, currency)
VALUES
    ('100', 100, 1, 'ads 100', 'https://www.img.com/1.png', 'https://www.google.ca', 5, 3, 3, now(), adddate(now(), interval 5 day ), 1000, 'XAF'),
    ('101', 100, 0, 'ads 100', 'https://www.img.com/1.png', 'https://www.google.ca', 5, 1, 1, '2010-01-01', null, 1000, 'XAF'),
    ('200', 200, 1, 'running', 'https://www.img.com/1.png', 'https://www.google.ca', 5, 3, 3, now(), adddate(now(), interval 5 day ), 1000, 'XAF'),
    ('201', 200, 2, 'ads 201', null, 'https://www.google.ca', 5, 1, 1, '2010-01-01', null, 1000, 'XAF'),
    ('202', 200, 2, 'ads 202', 'https://www.img.com/1.png', null, 5, 1, 1, '2010-01-01', null, 1000, 'XAF')
;