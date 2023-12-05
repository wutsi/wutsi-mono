INSERT INTO T_USER(id, name, email, full_name)
VALUES (100, 'john1', 'john1.partner@gmail.com', 'Jane Doe'),
       (200, 'john2', 'john2.partner@gmail.com', 'Jane Doe')
;

INSERT INTO T_WALLET(id, user_fk, currency, country)
VALUES ('100', 100, 'XAF', 'cm');

UPDATE T_USER
set wallet_id=100
where id = 100;

INSERT INTO T_STORE(id, user_fk, currency, feed_url, product_count, order_count, total_sales)
VALUES ('200', 200, 'XAF', 'https://www.goo.com/200.csv', 11, 111, 111000);

UPDATE T_USER
set store_id=200
where id = 200;
