INSERT INTO T_FUNDRAISING(id, account_id, business_id, currency, status, deactivated)
    VALUES
        (100, 100, 10, 'XAF', 1, null),
        (101, 101, 10, 'XAF', 1, null),
        (102, 101, 10, 'XAF', 3, null),
        (200, 200, 10, 'XAF', 2, null),
        (300, 300, 10, 'XAF', 3, now())
    ;
