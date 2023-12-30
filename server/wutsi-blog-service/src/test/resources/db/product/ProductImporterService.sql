INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (1, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe')
;

INSERT INTO T_STORE(id, user_fk, currency)
VALUES ('1', 1, 'XAF');

UPDATE T_USER
set store_id='1'
where id = 1;

INSERT INTO T_CATEGORY(id, level, title, long_title, parent_fk)
VALUES (1000, 0, 'Literature', 'Literature', null),
       (1001, 1, 'Autobiography', 'Literature > Autobiography', 1000);

INSERT INTO T_PRODUCT(id, external_id, store_fk, status, price, available, title, description, image_url, file_url)
VALUES (211, '200', '1', 0, 2000, true, 'update-me', null, 'https://picsum/100/100', 'https://file.com/file.pdf'),
       (311, '300', '1', 0, 3000, true, 'do-not-update-title', null, 'https://picsum/200', 'https://file.com/300.pdf'),
       (411, '400', '1', 1, 4000, false, 'product-400', null, 'https://picsum/400/400', 'https://file.com/400.pdf')
;
