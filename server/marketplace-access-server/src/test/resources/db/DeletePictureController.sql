INSERT INTO T_STORE(id, account_id, product_count, published_product_count, currency)
    VALUES
        (1, 1, 0, 0, 'XAF'),
        (2, 2, 3, 1, 'XAF')
    ;


INSERT INTO T_CATEGORY(id, parent_fk, title, title_french)
    VALUES
        (1100, null, 'Electronics', 'Électronique'),
        (1110, 1100, 'Computers', 'Ordinateurs')
    ;

INSERT INTO T_PRODUCT(id, store_fk, category_fk, status, is_deleted, title, summary, description, price, currency, quantity, published, deleted)
    VALUES
        (100, 1, 1110, 2, false, 'TV', 'summary of TV', 'description of TV', 150000, 'XAF', 10, now(), null),
        (200, 1, 1110, 1, false, 'TV', 'Sample TV', 'Long description', 50000, 'XAF', null, null, null),
        (300, 1, 1110, 1, false, 'TV', 'Sample TV', 'Long description', 50000, 'XAF', null, null, null)
    ;

INSERT INTO T_PICTURE(id, product_fk, url, hash, is_deleted, deleted)
    VALUES
        (101, 100, 'https://www.img.com/101.png', 'hash-101', false, null),
        (102, 100, 'https://www.img.com/102.png', 'hash-102', false, null),

        (201, 200, 'https://www.img.com/102.png', 'hash-102', false, null),
        (299, 200, 'https://www.img.com/199.png', 'hash-199', true, now()),

        (301, 300, 'https://www.img.com/102.png', 'hash-102', false, null),
        (302, 300, 'https://www.img.com/199.png', 'hash-199', false, null)
    ;

UPDATE T_PRODUCT set thumbnail_fk=101 WHERE id=100;
UPDATE T_PRODUCT set thumbnail_fk=201 WHERE id=200;
UPDATE T_PRODUCT set thumbnail_fk=301 WHERE id=300;
