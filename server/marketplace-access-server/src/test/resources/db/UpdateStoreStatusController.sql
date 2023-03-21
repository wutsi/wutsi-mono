INSERT INTO T_STORE(id, account_id, product_count, published_product_count, currency, status, deactivated)
    VALUES
        (100, 100, 10, 5, 'XAF', 1, null),
        (101, 101, 10, 5, 'XAF', 1, null),
        (102, 101, 10, 5, 'XAF', 3, null),
        (200, 200, 10, 5, 'XAF', 2, null),
        (300, 300, 10, 5, 'XAF', 3, now())
    ;
