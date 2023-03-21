INSERT INTO T_STORE(id, account_id, product_count, published_product_count, currency)
    VALUES
        (1, 11, 0, 0, 'XAF'),
        (2, 22, 3, 1, 'XAF')
    ;

INSERT INTO T_PRODUCT(id, store_fk, status, is_deleted, title, summary, description, price, currency, quantity, published, deleted, type)
    VALUES
        (100, 1, 2, false, 'TV', 'summary of TV', 'description of TV', 150000, 'XAF', 10, now(), null, 1)
    ;

INSERT INTO T_FILE(id, product_fk, name, url, content_type, content_size, is_deleted, deleted)
    VALUES
        (101, 100, 'File-301', 'https://www.img.com/301.png', 'image/png', 10240, false, null),
        (102, 100, 'File-302', 'https://www.img.com/302.pdf', 'application/pdf', 35000, false, null),
        (199, 100, '303', 'https://www.img.com/303.png', 'image/png', 1024, true, now())
    ;
