INSERT INTO T_STORE(id, account_id, product_count, published_product_count, currency)
    VALUES
        (1, 11, 0, 0, 'XAF'),
        (2, 22, 3, 1, 'XAF')
    ;

INSERT INTO T_PRODUCT(id, store_fk, status, is_deleted, title, summary, description, price, currency, quantity, published, deleted, type)
    VALUES
        (100, 1, 2, false, 'TV', 'summary of TV', 'description of TV', 150000, 'XAF', 10, now(), null, 1)
    ;
