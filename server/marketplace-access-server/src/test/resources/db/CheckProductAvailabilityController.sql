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

INSERT INTO T_PRODUCT(id, store_fk, category_fk, status, is_deleted, quantity)
    VALUES
        (100, 1, 1110, 2, false, 10),
        (101, 1, 1110, 2, false, null),
        (102, 1, 1120, 2, false, 1),
        (103, 1, 1120, 1, false, 10),
        (199, 1, 1110, 1, true, 1111)
    ;
