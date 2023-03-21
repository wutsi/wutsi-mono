INSERT INTO T_STORE(id, account_id, product_count, published_product_count, currency)
    VALUES
        (1, 11, 0, 0, 'XAF')
    ;

INSERT INTO T_DISCOUNT(id, store_fk, name, rate, starts, ends, all_products, is_deleted, deleted)
    VALUES
        (100, 1, 'FIN10', 10, '2020-01-01', '2020-01-30', true, false, null),
        (199, 1, 'FIN99', 99, '2020-01-01', '2020-01-30', true, true, now())
    ;
