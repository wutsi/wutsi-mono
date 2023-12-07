INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe')
;

INSERT INTO T_STORE(id, user_fk, currency, product_count, order_count, total_sales)
VALUES ('1', 100, 'XAF', 11, 111, 111000);

INSERT INTO T_PRODUCT(id, external_id, store_fk, title, description, image_url, file_url, available, order_count,
                      total_sales, price, file_content_type, file_content_length)
VALUES (1, '100', '1', 'product 1', 'description 1', 'https://picsum/100/100', 'https://file.com/file.pdf', true, 11,
        11000, 1000, 'application/pdf', 1000);
