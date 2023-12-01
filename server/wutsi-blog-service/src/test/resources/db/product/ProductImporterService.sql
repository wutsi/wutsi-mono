INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (1, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe')
;

INSERT INTO T_WALLET(id, user_fk, country, currency)
VALUES ('1', 1, 'cm', 'XAF');

UPDATE T_USER
set wallet_id='1'
where id = 1;

INSERT INTO T_PRODUCT(id, external_id, user_fk, title, description, price, currency, image_url, file_url)
VALUES (211, '200', 1, 'update-me', null, 100, 'XAF', 'https://picsum/100/100', 'https://file.com/file.pdf'),
       (311, '300', 1, 'do-not-update-title', 'do-not-update-descr', 300, 'XAF', 'https://picsum/200',
        'https://file.com/300.pdf');
