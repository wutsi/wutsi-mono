INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe');

INSERT INTO T_CATEGORY(id, parent_fk, level, title, long_title, title_french, title_french_ascii)
VALUES (1100, null, 0, 'Electronics', '', 'Électronique', 'Electronique'),
       (1110, 1100, 1, 'Computers', '', 'Ordinateurs', null);


INSERT INTO T_ADS(id, user_fk, status, title, image_url, url, type, cta_type, start_date, end_date, budget, currency, completed_date_time, category_fk)
VALUES
    ('100', 100, 1, 'ads 100', 'https://www.img.com/1.png', 'https://www.google.ca', 3, 3, now(), adddate(now(), interval 5 day ), 1000, 'XAF', now(), 1110)
;