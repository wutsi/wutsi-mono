INSERT INTO T_STORE(id, account_id, product_count, published_product_count, currency)
    VALUES
        (1, 11, 0, 0, 'XAF'),
        (2, 21, 0, 0, 'XAF'),
        (3, 30, 0, 0, 'XAF')
    ;

INSERT INTO T_PRODUCT(id, store_fk, category_fk, status, is_deleted, title, summary, description, price, currency, quantity, published, deleted)
    VALUES
        (100, 1, null, 2, false, 'TV', 'summary of TV', 'description of TV', 150000, 'XAF', 10, now(), null),
        (101, 1, null, 2, false, null, null, null, null, 'XAF', 11, now(), null),
        (102, 1, null, 2, false, null, null, null, null, 'XAF', 11, now(), null),
        (103, 1, null, 1, false, null, null, null, null, 'XAF', 11, now(), null),
        (199, 1, null, 1, true, 'TV', 'Sample TV', 'Long description', 150000, 'XAF', null, null, now()),
        (200, 2, null, 2, false, null, null, null, null, 'XAF', null, now(), null),
        (201, 2, null, 2, false, null, null, null, null, 'XAF', null, now(), null),
        (202, 2, null, 1, false, null, null, null, null, 'XAF', null, null, null)
    ;

INSERT INTO T_DISCOUNT(id, store_fk, type, name, rate, starts, ends, all_products, is_deleted, deleted)
    VALUES
        (100, 1, 1, 'FIN10', 10, '2020-01-01', '2020-01-30', false, false, null),
        (101, 1, 1, 'FIN25', 25, '2020-01-05', '2020-01-07', true, false, null),
        (102, 1, 1, 'FIN25', 25, '2021-01-05', '2021-01-07', true, false, null),
        (110, 1, 1, 'FIN50', 25, '2022-01-01', '2022-02-01', false, false, null),
        (199, 1, 1, 'FIN99', 99, '2020-01-01', '2020-01-30', true, true, now()),
        (200, 2, 1, 'FIN10', 10, '2020-01-01', '2020-01-30', true, false, null),
        (300, 3, 2, 'FIN10', 10, '2020-01-01', '2020-01-30', true, false, null)
    ;

INSERT INTO T_DISCOUNT_PRODUCT(discount_fk, product_fk)
    VALUES
        (100, 100),
        (100, 101),
        (100, 103),
        (100, 199),

        (110, 100),
        (110, 101)
    ;
