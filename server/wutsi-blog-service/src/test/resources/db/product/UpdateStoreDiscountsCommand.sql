INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe')
;

INSERT INTO T_STORE(id, user_fk, currency, product_count, order_count, total_sales)
VALUES ('1', 100, 'XAF', 11, 111, 111000);

