INSERT INTO T_STORE(id, account_id, product_count, published_product_count, currency)
    VALUES
        (1, 11, 0, 0, 'XAF'),
        (2, 22, 3, 1, 'XAF')
    ;

INSERT INTO T_CATEGORY(id, parent_fk, title, title_french)
    VALUES
        (1100, null, 'Electronics', 'Électronique'),
        (1110, 1100, 'Computers', 'Ordinateurs')
    ;

INSERT INTO T_PRODUCT(id, store_fk, category_fk, status, is_deleted, title, summary, description, price, currency, quantity, published, deleted, type)
    VALUES
        (100, 1, 1110, 2, false, 'TV', 'summary of TV', 'description of TV', 150000, 'XAF', 10, now(), null, 1),
        (200, 1, 1110, 2, false, 'TV', 'summary of TV', 'description of TV', 150000, 'XAF', 10, now(), null, 2),
        (300, 1, 1110, 2, false, 'TV', 'summary of TV', 'description of TV', 150000, 'XAF', null, now(), null, 3),
        (400, 1, 1110, 2, false, 'TV', 'summary of TV', 'description of TV', 150000, 'XAF', 0, now(), null, 1),
        (199, 1, 1110, 1, true, 'TV', 'Sample TV', 'Long description', 50000, 'XAF', null, null, now(), 1)
    ;

UPDATE T_PRODUCT
    SET
        event_meeting_provider_fk=1000,
        event_meeting_id='1234567890',
        event_meeting_password='123456',
        event_starts='2020-10-01 10:30',
        event_starts='2020-10-01 12:00',
        event_online=true,
        total_orders=100,
        total_sales=1500000,
        total_units=150,
        total_views=2000000
    WHERE id=200;

INSERT INTO T_FILE(id, product_fk, name, url, content_type, content_size, is_deleted, deleted)
    VALUES
        (301, 300, 'File-301', 'https://www.img.com/301.png', 'image/png', 10240, false, null),
        (302, 300, 'File-302', 'https://www.img.com/302.pdf', 'application/pdf', 35000, false, null),
        (303, 300, '303', 'https://www.img.com/303.png', 'image/png', 1024, true, now())
    ;

INSERT INTO T_PICTURE(id, product_fk, url, hash, is_deleted, deleted)
    VALUES
        (101, 100, 'https://www.img.com/101.png', 'hash-101', false, null),
        (102, 100, 'https://www.img.com/102.png', 'hash-102', false, null),
        (199, 100, 'https://www.img.com/199.png', 'hash-199', true, now())
    ;

UPDATE T_PRODUCT set thumbnail_fk=101 WHERE id=100;
