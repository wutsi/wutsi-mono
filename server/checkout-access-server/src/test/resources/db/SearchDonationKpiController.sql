INSERT INTO T_BUSINESS(id, account_id, status, deactivated,currency, country)
    VALUES
        (1, 1, 1, null, 'XAF', 'CM'),
        (2, 2, 1, null, 'XAF', 'CM')
    ;

INSERT INTO T_KPI_DONATION(business_fk, date, total_donations, total_value)
    VALUES
        (1, now(), 5, 30000),
        (1, DATE_ADD(now(), INTERVAL -1 DAY), 1, 2000),
        (1, DATE_ADD(now(), INTERVAL -2 DAY), 3, 3000),
        (1, DATE_ADD(now(), INTERVAL -10 DAY), 11, 50000),
        (2, DATE_ADD(now(), INTERVAL -2 DAY), 1, 10000)
    ;
