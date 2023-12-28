INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe'),
       (200, false, 'john2', 'john2.partner@gmail.com', 'Jane Doe'),
       (300, false, 'john3', 'john3.partner@gmail.com', 'Jane Doe');

INSERT INTO T_STORE(id, user_fk, currency, subscriber_discount, first_purchase_discount)
VALUES ('1', 100, 'XAF', 10, 0),
       ('2', 200, 'XAF', 5, 20),
       ('3', 300, 'XAF', 0, 0);

INSERT INTO T_PRODUCT(id, external_id, store_fk, status, title, image_url, file_url, available, price)
VALUES (101, '101', '1', 1, 'product 101', 'https://picsum/101', 'https://file.com/101.pdf', true, 1000),
       (102, '102', '1', 1, 'product 102', 'https://picsum/102', 'https://file.com/102.pdf', false, 2000),
       (103, '103', '1', 0, 'product 103', 'https://picsum/103', 'https://file.com/102.pdf', true, 500),
       (201, '201', '2', 1, 'product 201', 'https://picsum/201', 'https://file.com/201.pdf', true, 1500),
       (301, '301', '3', 0, 'product 301', 'https://picsum/301', 'https://file.com/301.pdf', true, 500);

INSERT INTO T_STORY(id, user_fk, title)
VALUES (1, 100, 'This is PRODUCT 103'),
       (2, 100, 'Sample Story')
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk)
VALUES (200, 300);
