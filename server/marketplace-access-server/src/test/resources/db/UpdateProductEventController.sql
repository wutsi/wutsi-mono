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
        (100, 1, null, 2, false, 'TV', 'summary of TV', 'description of TV', 150000, 'XAF', 10, now(), null)
    ;
