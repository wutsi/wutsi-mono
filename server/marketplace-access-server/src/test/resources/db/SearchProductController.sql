INSERT INTO T_STORE(id, account_id, product_count, published_product_count, currency, status, deactivated)
    VALUES
        (1, 1, 0, 0, 'XAF', 1, null),
        (2, 2, 3, 1, 'XAF', 3, now())
    ;

INSERT INTO T_CATEGORY(id, parent_fk, title, title_french)
    VALUES
        (1100, null, 'Electronics', 'Électronique'),
        (1110, 1100, 'Computers', 'Ordinateurs'),
        (1120, 1100, 'Networking', null)
    ;

INSERT INTO T_PRODUCT(id, store_fk, category_fk, type, status, is_deleted, title, summary, description, price, currency, quantity, published, deleted)
    VALUES
        (100, 1, 1110, 2, 2, false, 'TV', 'summary of TV', 'description of TV', 150000, 'XAF', 10, now(), null),
        (101, 1, 1110, 3, 2, false, null, null, null, null, 'XAF', 11, now(), null),
        (102, 1, 1120, 3, 2, false, null, null, null, null, 'XAF', 11, now(), null),
        (103, 1, 1120, 1, 1, false, null, null, null, null, 'XAF', 11, now(), null),
        (199, 1, 1110, 1, 1, true, 'TV', 'Sample TV', 'Long description', 50000, 'XAF', null, null, now()),
        (200, 2, 1100, 1, 2, false, null, null, null, null, 'XAF', null, now(), null),
        (201, 2, 1100, 1, 2, false, null, null, null, null, 'XAF', null, now(), null),
        (202, 2, 1100, 1, 1, false, null, null, null, null, 'XAF', null, null, null)
    ;

INSERT INTO T_PICTURE(id, product_fk, url, hash, is_deleted, deleted)
    VALUES
        (101, 100, 'https://www.img.com/101.png', 'hash-101', false, null),
        (102, 100, 'https://www.img.com/102.png', 'hash-102', false, null),
        (199, 100, 'https://www.img.com/199.png', 'hash-199', true, now())
    ;

UPDATE T_PRODUCT set thumbnail_fk=101 WHERE id=100;
