INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe');

INSERT INTO T_ADS(id, user_fk, status, title, image_url, url, duration_days)
VALUES
    ('100', 100, 0, 'ads 100', 'https://www.img.com/1.png', 'https://www.google.ca', 5),

    ('900', 100, 1, 'running', 'https://www.img.com/1.png', 'https://www.google.ca', 5),
    ('901', 100, 0, 'running', null, 'https://www.google.ca', 5),
    ('902', 100, 0, 'ads 100', 'https://www.img.com/1.png', null, 5)
;