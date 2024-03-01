INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES
    (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe'),
    (200, false, 'john2', 'john2.partner@gmail.com', 'Jane Doe');

INSERT INTO T_ADS(id, user_fk, status, title, image_url, url, duration_days, type, cta_type, start_date, end_date)
VALUES
    ('100', 100, 1, 'ads 100', 'https://www.img.com/1.png', 'https://www.google.ca', 5, 3, 3, '2020-10-12', '2020-10-17'),
    ('101', 100, 0, 'ads 101', 'https://www.img.com/1.png', 'https://www.google.ca', 5, 1, 1, null, null),
    ('102', 100, 1, 'ads 102', 'https://www.img.com/1.png', 'https://www.google.ca', 5, 3, 3, '2020-10-10', '2020-10-15'),
    ('200', 200, 1, 'running', 'https://www.img.com/1.png', 'https://www.google.ca', 5, 3, 3, '2020-10-10', '2020-11-10'),
    ('201', 200, 2, 'ads 201', null, 'https://www.google.ca', 5, 1, 1, '2020-10-01', '2020-11-05'),
    ('202', 200, 2, 'ads 202', 'https://www.img.com/1.png', null, 5, 1, 1, null, null)
;