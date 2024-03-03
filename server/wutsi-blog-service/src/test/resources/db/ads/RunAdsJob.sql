INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES
    (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe'),
    (200, false, 'john2', 'john2.partner@gmail.com', 'Jane Doe');

INSERT INTO T_ADS(id, user_fk, status, title, image_url, url,  type, cta_type, start_date, end_date, budget, currency)
VALUES
    ('100', 100, 1, 'ads 100', 'https://www.img.com/1.png', 'https://www.google.ca',  1, 1, '2020-10-20', '2020-10-25', 10000, 'XAF'),
    ('101', 100, 1, 'ads 100', 'https://www.img.com/1.png', 'https://www.google.ca',  3, 3, '2020-10-22', '2020-10-27', 20000, 'XAF'),
    ('102', 100, 0, 'ads 102', 'https://www.img.com/1.png', 'https://www.google.ca',  3, 3, null, null, 0, 'XAF'),
    ('200', 200, 2, 'running', 'https://www.img.com/1.png', 'https://www.google.ca',  3, 3, '2020-10-10', '2020-11-10', 1000, 'XAF'),
    ('201', 200, 3, 'ads 201', null, 'https://www.google.ca',  1, 1, '2020-10-01', '2020-11-05', 1000, 'XAF'),
    ('202', 200, 3, 'ads 202', 'https://www.img.com/1.png', null,  1, 1, now(), null, 1000, 'XAF')
;