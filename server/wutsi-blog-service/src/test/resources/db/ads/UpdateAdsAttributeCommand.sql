INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe');

INSERT INTO T_ADS(id, user_fk, status, type, title, image_url, url, start_date, end_date, budget)
VALUES
    ('100', 100, 0, 1, 'ads 100', 'https://www.img.com/1.png', 'https://www.google.ca', '2024-10-01', '2024-10-20', 1000),
    ('110', 100, 0, 1, 'update-start-date', 'https://www.img.com/1.png', 'https://www.google.ca', '2024-10-01', '2024-10-20', 1000),
    ('111', 100, 0, 1, 'update-end-date', 'https://www.img.com/1.png', 'https://www.google.ca', '2024-10-01', '2024-10-20', 1000),
    ('112', 100, 0, 1, 'update-type', 'https://www.img.com/1.png', 'https://www.google.ca', '2024-10-01', '2024-10-20', 1000),
    ('113', 100, 1, 1, 'running', 'https://www.img.com/1.png', 'https://www.google.ca', '2024-10-01', '2024-10-20', 1000)
;

INSERT INTO T_CATEGORY(id, title, title_french, title_french_ascii, level, long_title, long_title_french, parent_fk)
VALUES
    (100, 'Art', 'Art', 'Art', 0, 'Art', 'Art', null);