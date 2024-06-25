INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe')
;

INSERT INTO T_STORE(id, user_fk, currency, product_count, order_count, total_sales)
VALUES ('1', 100, 'XAF', 11, 111, 111000);

INSERT INTO T_PRODUCT(id, store_fk, title, description, number_of_pages)
VALUES (1, '1', 'product 1', 'description 1', 3);

INSERT INTO T_PAGE(product_fk, number, content_type, content_url) VALUES
    (1, 1, 'image/png', 'https://www.img.com/1.png'),
    (1, 2, 'image/png', 'https://www.img.com/2.png'),
    (1, 3, 'image/png', 'https://www.img.com/3.png');
